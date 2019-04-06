package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.Member;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@JsonIgnoreProperties
public class Peer implements UserDetails {

    private String id;
    private String role;
    private String address;
    private String location;
    private String email;
    private String password;

    public Peer() {
    }

    public Peer(String id) {
        this.id = id;
    }

    public Peer(eu.smartsocietyproject.pf.Peer peer) {
        this.id = peer.getId();
        JsonNode jsonNode = peer.getData();
        this.role = jsonNode.get("role").asText();
        this.address = jsonNode.get("address").asText();
        this.password = jsonNode.get("password").asText();
        this.email = jsonNode.get("email").asText();
    }

    public Peer(String id, String role, String address) {
        this.id = id;
        this.role = role;
        this.address = address;
    }

    public static Peer of(Member member) {
        return new Peer(member.getPeerId(), member.getRole(), member.getAddress());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.id;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
