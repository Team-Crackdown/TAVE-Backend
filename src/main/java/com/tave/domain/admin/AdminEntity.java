package com.tave.domain.admin;

import com.tave.constant.Role;
import com.tave.domain.TimeStamp;
import com.tave.domain.UserEntity;
import com.tave.domain.team.TeamEntity;
import com.tave.dto.admin.AdminDto;
import com.tave.repository.team.TeamRepository;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class AdminEntity extends TimeStamp implements UserEntity {

    @Id
    @Column(name = "admin_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private final Role role = Role.ADMIN;

    private String password;

    private String username; //회원 아이디

    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "admin")
    private List<TeamEntity> teams = new ArrayList<>();

}
