package io.bryma.betim.swine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.services.PeerService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/peers")
public class PeerController {

    private PeerService peerService;
    private AuthenticationManager authenticationManager;

    public PeerController(PeerService peerService, AuthenticationManager authenticationManager) {
        this.peerService = peerService;
        this.authenticationManager = authenticationManager;
    }

    @Value("${jwt.secret}")
    private String secret;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Peer peer){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(peer.getId(), peer.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = now.plusMinutes(15);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", peer.getId());
        String jwt = "Peer "+ Jwts.builder().setSubject(peer.getId()).setClaims(claims)
                .setIssuedAt(Date.from((now.atZone(ZoneId.systemDefault()).toInstant())))
                .setExpiration(Date.from((expire.atZone(ZoneId.systemDefault()).toInstant())))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Peer peer){
        if(!validate(peer))
            return ResponseEntity.badRequest().build();

        try {
            peerService.save(peer);
            return login(peer);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean validate(Peer peer){
        return peer != null && peer.getId() != null && !peer.getId().isEmpty() && peer.getPassword() != null && !peer.getEmail().isEmpty()
                && !peer.getPassword().isEmpty() && peer.getAddress() != null && !peer.getAddress().isEmpty();
    }

}
