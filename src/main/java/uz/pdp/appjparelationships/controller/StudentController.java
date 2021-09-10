package uz.pdp.appjparelationships.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final AddressRepository addressRepository;
    private final SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT
    @GetMapping("forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable(value = "facultyId") Integer facultyId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping("forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable(value = "groupId") Integer groupId,
                                                @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }

    @PostMapping("/add")
    public String add(@RequestBody StudentDto dto) {

        List<Subject> subjects = new ArrayList<>();

        for (Integer subject : dto.getSubjectsId()) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subject);
            optionalSubject.ifPresent(subjects::add);
        }
        if (subjects.isEmpty()) return "Subject topilmadi";
        Optional<Address> optionalAddress = addressRepository.findById(dto.getAddressId());
        if (!optionalAddress.isPresent()) return "Address topilmadi";
        Optional<Group> optionalGroup = groupRepository.findById(dto.getGroupId());
        if (!optionalGroup.isPresent()) return "Group topilmadi";

        Student student = new Student();
        student.setAddress(optionalAddress.get());
        student.setGroup(optionalGroup.get());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student malumotlari saqlandi";
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable(value = "id")Integer id,StudentDto dto){

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) return "Student topilmadi";

        List<Subject> subjects = new ArrayList<>();

        for (Integer subject : dto.getSubjectsId()) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subject);
            optionalSubject.ifPresent(subjects::add);
        }
        if (subjects.isEmpty()) return "Subject topilmadi";
        Optional<Address> optionalAddress = addressRepository.findById(dto.getAddressId());
        if (!optionalAddress.isPresent()) return "Address topilmadi";
        Optional<Group> optionalGroup = groupRepository.findById(dto.getGroupId());
        if (!optionalGroup.isPresent()) return "Group topilmadi";

        Student student = optionalStudent.get();
        student.setAddress(optionalAddress.get());
        student.setGroup(optionalGroup.get());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setSubjects(subjects);
        studentRepository.save(student);
        return "Student malumotlari yangilandi";
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable(value = "id")Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) return false;
        studentRepository.delete(optionalStudent.get());
        return true;
    }
}
