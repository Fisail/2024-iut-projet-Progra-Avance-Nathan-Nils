package iut.nantes.project.gateway.Repository

import iut.nantes.project.gateway.Entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByLogin(login: String): Optional<UserEntity>
}
