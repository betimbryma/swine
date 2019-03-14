package io.bryma.betim.swine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Document(collection = "peers")
public class Peer {

    @Id
    private String id;
    private String peerId;
    private String role;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return id.equals(peer.id) &&
                peerId.equals(peer.peerId) &&
                role.equals(peer.role) &&
                password.equals(peer.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, peerId, role, password);
    }

}
