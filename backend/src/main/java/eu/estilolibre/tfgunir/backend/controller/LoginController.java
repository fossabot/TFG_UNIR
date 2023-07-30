package eu.estilolibre.tfgunir.backend.controller;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.estilolibre.tfgunir.backend.model.Usuario;
import eu.estilolibre.tfgunir.backend.repository.UsuarioRepository;
import eu.estilolibre.tfgunir.backend.security.TokenService;
import io.jsonwebtoken.Jwts;

@CrossOrigin(origins = { "http://localhost:4200", "http://localhost:3000", "http://localhost:5173" })
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private long expiracion = 3600000;
    private String prefijoToken = "Bearer";
    private String cabeceraHttp = "Authorization";
    @Autowired
    private UsuarioRepository repository;

    @PostMapping("")
    public ResponseEntity auth(@RequestBody FormUser login) {
        // buscar usuario en BBDD
        List<Usuario> result = repository.findByEmail(login.getEmail());

        if (result.isEmpty()) {
            return new ResponseEntity<String>("{'message':'no autorizado'}", HttpStatus.UNAUTHORIZED);
        } else {
            String result_pass = result.get(0).getPassword();
            if (login.getPassword().equals(result_pass)) {
                String token = getJWTToken(login.getEmail());
                User user = new User();
                user.setUser(login.getEmail());
                user.setFullname(result.get(0).getNombre() + " " + result.get(0).getApellidos());
                user.setToken(token);
                return ResponseEntity.ok(user);
            }
            return new ResponseEntity<String>("{'message':'no autorizado'}", HttpStatus.UNAUTHORIZED);

        }

    }

    private String getJWTToken(String username) {
        String secretKey = "813cef5f-3459-4618-87a6-a69e2a1296d4_mySecretKey_mySecretKey";

        String JWT = new TokenService().crearToken(username, secretKey,
                new Date(System.currentTimeMillis() + expiracion));

        return prefijoToken + " " + JWT;
    }
}
