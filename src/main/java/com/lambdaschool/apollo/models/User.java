package com.lambdaschool.apollo.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * The entity allowing interaction with the users table
 */
@ApiModel(value = "User",
        description = "Yes, this is an actual user")
@Entity
@Table(name = "users")
public class User
        extends Auditable {
    /**
     * The primary key (long) of the users table.
     */
    @ApiModelProperty(name = "user id",
            value = "primary key for User",
            required = true,
            example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userid;

    /**
     * The username (String). Cannot be null and must be unique
     */
    @ApiModelProperty(name = "User Name",
            value = "Actual user name for sign on",
            required = true,
            example = "Some Name")
    @Size(min = 2,
            max = 30,
            message = "User Name must be between 2 and 30 characters")
    @NotNull
    @Column(nullable = false,
            unique = true)
    private String username;

    /**
     * Primary email account of user. Could be used as the userid. Cannot be null and must be unique.
     */
    @ApiModelProperty(name = "primary email",
            value = "The email for this user",
            required = true,
            example = "john@lambdaschool.com")
    @NotNull
    @Column(nullable = false,
            unique = true)
    @Email
    private String primaryemail;


    /**
     * This field captures which topics a user owns
     */
    @ApiModelProperty(name = "roles",
            value = "List of user roles for this users")
    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "user",
            allowSetters = true)
    @JsonIgnore
    private List<UserRoles> roles = new ArrayList<>();

    @ApiModelProperty(name = "owner topics", value = "List of topics owned by this user (Topic Leader)")
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = {"owner", "users"}, allowSetters = true)
    @JsonIgnore
    private List<Topic> ownedtopics = new ArrayList<>();

    /**
     * This field represents which topics a user is a member of
     * EXCLUSIVE of owned topics
     */
    @ApiModelProperty(name = "member topics", value = "List of topics this user is a member of (Topic Member)")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "user", allowSetters = true)
    @JsonIgnore
    private List<TopicUsers> topics = new ArrayList<>();

    /**
     * Default constructor used primarily by the JPA.
     */
    public User() {
    }

    /**
     * Given the params, create a new user object
     * <p>
     * userid is autogenerated
     *
     * @param username     The username (String) of the user
     * @param primaryemail The primary email (String) of the user
     */

    public User(@Size(min = 2,
            max = 30,
            message = "User Name must be between 2 and 30 characters") @NotNull String username, @NotNull @Email String primaryemail) {
        this.username = username;
        this.primaryemail = primaryemail;
    }

    /**
     * Getter for userid
     *
     * @return the userid (long) of the user
     */
    public long getUserid() {
        return userid;
    }

    /**
     * Setter for userid. Used primary for seeding data
     *
     * @param userid the new userid (long) of the user
     */
    public void setUserid(long userid) {
        this.userid = userid;
    }

    /**
     * Getter for username
     *
     * @return the username (String) lowercase
     */
    public String getUsername() {
        if (username == null) // this is possible when updating a user
        {
            return null;
        } else {
            return username.toLowerCase();
        }
    }

    /**
     * setter for username
     *
     * @param username the new username (String) converted to lowercase
     */
    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    /**
     * getter for primary email
     *
     * @return the primary email (String) for the user converted to lowercase
     */
    public String getPrimaryemail() {
        if (primaryemail == null) // this is possible when updating a user
        {
            return null;
        } else {
            return primaryemail.toLowerCase();
        }
    }

    /**
     * setter for primary email
     *
     * @param primaryemail the new primary email (String) for the user converted to lowercase
     */
    public void setPrimaryemail(String primaryemail) {
        this.primaryemail = primaryemail.toLowerCase();
    }

    /**
     * Getter for user role combinations
     *
     * @return A list of user role combinations associated with this user
     */
    public List<UserRoles> getRoles() {
        return roles;
    }

    /**
     * Setter for user role combinations
     *
     * @param roles Change the list of user role combinations associated with this user to this one
     */
    public void setRoles(List<UserRoles> roles) {
        this.roles = roles;
    }

    /**
     * Add one role to this user
     *
     * @param role the new role (Role) to add
     */
    public void addRole(Role role) {
        roles.add(new UserRoles(this,
                role));
    }

    @JsonGetter
    public List<TopicUsers> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicUsers> topics) {
        this.topics = topics;
    }

    public List<Topic> getOwnedtopics() {
        return ownedtopics;
    }

    public void setOwnedtopics(List<Topic> ownedtopics) {
        this.ownedtopics = ownedtopics;
    }

    /**
     * Internally, user security requires a list of authorities, roles, that the user has. This method is a simple way to provide those.
     * Note that SimpleGrantedAuthority requests the format ROLE_role name all in capital letters!
     *
     * @return The list of authorities, roles, this user object has
     */
    @JsonIgnore
    public List<SimpleGrantedAuthority> getAuthority() {
        List<SimpleGrantedAuthority> rtnList = new ArrayList<>();

        for (UserRoles r : this.roles) {
            String myRole = "ROLE_" + r.getRole()
                    .getName()
                    .toUpperCase();
            rtnList.add(new SimpleGrantedAuthority(myRole));
        }

        return rtnList;
    }


    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                '}';
    }
}
