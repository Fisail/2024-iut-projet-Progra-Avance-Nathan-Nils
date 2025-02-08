import iut.nantes.project.gateway.GatewayApplication
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(classes = [GatewayApplication::class])
class SecurityTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply { springSecurity() }.build()
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = ["ADMIN"])
    fun `test get all products with admin role`() {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8081/api/v1/products")
            .header("X-User", "AdminUser"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    @WithMockUser(username = "USER", roles = ["USER"])
    fun `test access denied for non-admin user`() {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8081/api/v1/products")
            .header("X-User", "User"))
            .andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    @Test
    fun `test create user endpoint accessible by all`() {
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/api/v1/user")
            .contentType("application/json")
            .content("{\"login\": \"test\", \"password\": \"test\", \"isAdmin\": false}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
