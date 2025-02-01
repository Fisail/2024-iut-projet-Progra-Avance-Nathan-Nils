package iut.nantes.project.gateway.Service

import iut.nantes.project.gateway.Entity.UserDTO
import iut.nantes.project.gateway.Entity.UserEntity
import iut.nantes.project.gateway.Repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

class UserServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = BCryptPasswordEncoder()
    private val userService = UserService(userRepository)

    @Test
    fun `test createUser with encoded password`() {
        val userDTO = UserDTO("testuser", "password", false)
        val userEntity = UserEntity(null, "testuser", passwordEncoder.encode("password"), false)

        `when`(userRepository.save(any(UserEntity::class.java))).thenReturn(userEntity)

        val result = userService.createUser(userDTO)

        assertNotNull(result)
        assertEquals("testuser", result.login)
        assertTrue(passwordEncoder.matches("password", result.password))
    }

    @Test
    fun `test findByLogin `() {
        val userEntity = UserEntity(1L, "testuser", "password", false)
        `when`(userRepository.findByLogin("testuser")).thenReturn(Optional.of(userEntity))

        val result = userService.findByLogin("testuser")

        assertTrue(result.isPresent)
        assertEquals("testuser", result.get().login)
    }
}
