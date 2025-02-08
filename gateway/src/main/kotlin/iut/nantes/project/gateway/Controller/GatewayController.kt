package iut.nantes.project.gateway.Controller

import iut.nantes.project.gateway.Entity.UserDTO
import iut.nantes.project.gateway.Service.UserService
import iut.nantes.project.products.DTO.ProductDTO
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient

@RestController
@EnableMethodSecurity
@RequestMapping("/api/v1")
class GatewayController(private val userService: UserService) {

    private val webClient: WebClient = WebClient.create()

    @PostMapping("/user")
    fun createUser(@RequestBody userDTO: UserDTO): ResponseEntity<String> {
        userService.createUser(userDTO)
        return ResponseEntity.ok("Utilisateur crée !")
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllProductWithRoleAdmin(@RequestHeader("X-User") user: String): ResponseEntity<List<ProductDTO>> {
        val response = webClient.get().uri("http://localhost:8081/api/v1/products")
            .header("X-User", user)
            .retrieve()
            .bodyToFlux(ProductDTO::class.java)
            .collectList()
            .block()
        return ResponseEntity.ok(response)
    }
}
