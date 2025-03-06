package br.org.institutobushido.services.admin;

import br.org.institutobushido.controllers.dtos.admin.AdminDTOResponse;
import br.org.institutobushido.controllers.dtos.admin.login.LoginDTOResponse;
import br.org.institutobushido.controllers.dtos.admin.signup.SignUpDTORequest;
import br.org.institutobushido.models.admin.Admin;
import br.org.institutobushido.providers.enums.admin.UserRole;
import br.org.institutobushido.providers.utils.resources.exceptions.AlreadyRegisteredException;
import br.org.institutobushido.providers.utils.resources.exceptions.EntityNotFoundException;
import br.org.institutobushido.repositories.AdminRepositorio;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class AdminServicesTest {

    private UserDetails expectedUserDetails;

    private SignUpDTORequest signUpDTORequest;

    @Mock
    private AdminRepositorio adminRepositorio;

    @Mock
    private Algorithm algorithmMock;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private AdminServices adminServices;

    private Admin admin;

    @Mock
    private Algorithm algorithm;

    @BeforeEach
    void setUp() {
        adminServices = new AdminServices(adminRepositorio, mongoTemplate);

        admin = new Admin("admin", "admin@email.com", "admin", "admin", UserRole.ADMIN);

        signUpDTORequest = new SignUpDTORequest(
                "admin",
                "admin@email.com",
                "admin",
                "cargo",
                UserRole.ADMIN);

        expectedUserDetails = new Admin("admin", "admin@email.com", "admin", "admin", UserRole.ADMIN);
    }

    @Test
    void deveRetornarErroQuandoAdminJaCadastrado() {

        // Act
        adminServices.signup(signUpDTORequest);

        // Assert
        when(adminRepositorio.findByEmail(anyString())).thenReturn(expectedUserDetails);

        assertThrows(
                AlreadyRegisteredException.class,
                () -> adminServices.signup(signUpDTORequest)
        );
    }

    @Test
    void deveAdicionarAdminQuandoNaoCadastrado() {
        adminServices.signup(signUpDTORequest);
        when(adminRepositorio.findByEmail(anyString())).thenReturn(null);
        when(adminRepositorio.save(any(Admin.class))).thenReturn(admin);
        assertDoesNotThrow(() -> adminServices.signup(signUpDTORequest));
    }

    @Test
    @Disabled("Teste pendente")
    void testLogin() {
        // Mock the generateToken method
        AdminServices spy = Mockito.spy(adminServices);
        Mockito.when(spy.generateToken(admin)).thenReturn("mockedToken");
        LoginDTOResponse loginDTOResponse = spy.login(admin);

        assertNotNull(loginDTOResponse);
        assertEquals("token", loginDTOResponse.token());
        assertEquals(UserRole.ADMIN.getValue(), loginDTOResponse.role());
    }

    @Test
    void deveCarregarDetalhesDoAdmin() {
        // Arrange
        String validEmail = "valid@example.com";

        when(adminRepositorio.findByEmail(validEmail)).thenReturn(expectedUserDetails);

        adminServices = new AdminServices(adminRepositorio, mongoTemplate);

        UserDetails actualUserDetails = adminServices.loadUserByUsername(validEmail);

        assertEquals(expectedUserDetails, actualUserDetails);
    }

    @Test
    void deveLancarExcecaoAoCarregarDetalhesDoAdminComEmailInvalido() {
        // Arrange
        String invalidEmail = "invalid@example.com";

        // Mock the adminRepositorio.findByEmail() method to return null
        when(adminRepositorio.findByEmail(invalidEmail)).thenReturn(null);

        // Create an instance of AdminServices
        adminServices = new AdminServices(adminRepositorio, mongoTemplate);

        // Act and Assert
        assertThrows(EntityNotFoundException.class, () -> {
            adminServices.loadUserByUsername(invalidEmail);
        });
    }

    @Test
    void deveRetornarListaAdminDTOResponseQuandoNomeValido() {
        // Arrange
        String nome = "John";

        when(mongoTemplate.find(any(Query.class), eq(Admin.class))).thenReturn(List.of(admin));

        // Act
        List<AdminDTOResponse> result = adminServices.buscarAdminPorNome(nome);

        // Assert
        assertEquals(1, result.size());
        assertEquals(result.get(0).nome(), admin.getNome());
        assertEquals(result.get(0).email(), admin.getEmail());
        assertEquals(result.get(0).cargo(), admin.getCargo());
    }

    @Test
    void deveLancarExcecaoQuandoNomeInvalido() {
        // Arrange
        String nome = "invalid";
        List<AdminDTOResponse> expected = new ArrayList<>();
        when(mongoTemplate.find(any(Query.class), eq(Admin.class))).thenReturn(new ArrayList<>());

        // Act
        List<AdminDTOResponse> result = adminServices.buscarAdminPorNome(nome);

        // Assert
        assertEquals(expected, result);
    }
}
