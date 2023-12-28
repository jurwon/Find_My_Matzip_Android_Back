package com.matzip.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.matzip.constant.UserRole;
import com.matzip.dto.UsersFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 프록시 객체를 직렬화에서 제외  대신에 레스토랑dto2만듦
@Entity
@Table(name="users")
@Getter
@Setter
@ToString
public class Users {

    @Id
    @Column(name="userid",unique = true)
    private String userid;       //회원 id

    @Column(nullable = false)
    private String user_pwd; //회원 pw

    @Column(nullable = false)
    private String username; //회원 이름

    private String user_address; //주소

    private String userphone; //전화번호

    @Enumerated(EnumType.STRING)
    private UserRole user_role; //역할(ADMIN,USER)

    private String user_image; //프로필 이미지

    private String gender;
    //private LocalDateTime regTime; //가입 날짜

    @JsonIgnore
    @OneToMany(mappedBy = "toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "feelingUsers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feeling> UsersFeeling = new ArrayList<>();





    public static Users createUsers(UsersFormDto usersFormDto, PasswordEncoder passwordEncoder){
        Users users = new Users();
        users.setUserid(usersFormDto.getUserid());
        String user_pwd = passwordEncoder.encode(usersFormDto.getUser_pwd());
        users.setUser_pwd(user_pwd);
        users.setUsername(usersFormDto.getUsername());
        users.setUser_address(usersFormDto.getUser_address());
        users.setUserphone(usersFormDto.getUserphone());
        users.setUser_role(UserRole.USER);
        users.setUser_image(usersFormDto.getUser_image());
        users.setGender(usersFormDto.getGender());
        return users;
    }

    public void updateUsers(UsersFormDto usersFormDto){
        this.username = usersFormDto.getUsername();
        this.user_address = usersFormDto.getUser_address();
        this.userphone = usersFormDto.getUserphone();
        this.user_image = usersFormDto.getUser_image();
        this.gender=usersFormDto.getGender();
    }

    public static Users aboutUsers(UsersFormDto usersFormDto){
        Users users = new Users();
        users.setUserid(usersFormDto.getUserid());
        users.setUsername(usersFormDto.getUsername());
        users.setUser_address(usersFormDto.getUser_address());
        users.setUserphone(usersFormDto.getUserphone());
        users.setUser_role(UserRole.ADMIN);
        users.setUser_image(usersFormDto.getUser_image());
        users.setGender(usersFormDto.getGender());
        return users;
    }

    //encoding된 pw비교
    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.user_pwd);
    }
}
