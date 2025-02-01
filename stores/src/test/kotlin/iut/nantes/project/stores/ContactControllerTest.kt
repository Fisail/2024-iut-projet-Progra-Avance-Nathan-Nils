package iut.nantes.project.stores

import io.mockk.InternalPlatformDsl.toArray
import iut.nantes.project.stores.Controller.ContactController
import iut.nantes.project.stores.DTO.ContactDTO
import iut.nantes.project.stores.DTO.AddressDTO
import iut.nantes.project.stores.Exception.ContactException
import iut.nantes.project.stores.Repository.StoreRepository
import iut.nantes.project.stores.Service.ContactService
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @MockBean
    private lateinit var contactService: ContactService

    val contactDto = ContactDTO(1, "test.contact@gmail.com", "0123456789", AddressDTO("7 rue du test", "Nantes", "44200"))

    @Test
    fun createValidContact() {
        whenever(contactService.createContact(any())).thenReturn(contactDto)

        mockMvc.perform(
            post("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "test.contact@gmail.com", "phone": "0123456789", "address": {"street" : "7 rue du test", "city":"Nantes", "postalCode": "44200"}}"""))
            .andExpect(status().isCreated)
    }

    //Pb ne prend pas en compte les formatages des paramètres
    @Test
    fun `test create invalid contact`() {
        mockMvc.perform(
            post("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "test.contact@gmail.com", "phone": "0123456789", "address": {"street" : "7", "city":"T", "postalCode": "4"}}"""))
            .andExpect(status().`is`(400))
    }

    @Test
    fun `test get contact by city`() {
        whenever(contactService.getContactsByCity(any())).thenReturn(listOf(contactDto, contactDto))

        mockMvc.perform(
            get("/api/v1/contacts?city=Nantes"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].address.city").value("Nantes"))
    }

    @Test
    fun `test get contact by valid ID`() {
        whenever(contactService.getContactById(any())).thenReturn(contactDto)

        mockMvc.perform(
            get("/api/v1/contacts/1"))
            .andExpect(status().isOk)
    }

    @Test
    fun `test get contact by invalid ID`() {
        whenever(contactService.getContactById(any())).thenThrow(ContactException.ContactNotFoundException())

        mockMvc.perform(
            get("/api/v1/contacts/2"))
            .andExpect(status().`is`(404))
    }

    @Test
    fun `test get contact by Illegal ID`() {
        whenever(contactService.getContactById(any())).thenThrow(ContactException.InvalidIdFormatException())

        mockMvc.perform(
            get("/api/v1/contacts/a"))
            .andExpect(status().`is`(400))
    }

    @Test
    fun `test update valid contact`() {
        whenever(contactService.updateContact(eq(1), any())).thenReturn(contactDto)

        mockMvc.perform(
            put("/api/v1/contacts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "test.contact@gmail.com", "phone": "0123456789", "address": {"street" : "7 rue du test", "city":"Nantes", "postalCode": "44200"}}"""))
            .andExpect(status().isOk)
    }

    @Test
    fun `test update invalid contact`() {
        whenever(contactService.updateContact(eq(1), any())).thenThrow(ContactException.InvalidDataException())

        mockMvc.perform(
            put("/api/v1/contacts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"email": "updatedtest.contact@gmail.com", "phone": "9876543210", "address": {"street" : "7 rue du test", "city":"Nantes", "postalCode": "44200"}}"""))
            .andExpect(status().`is`(400))
    }

    @Test
    fun `test delete valid contact`() {
        doNothing().whenever(contactService).deleteContact(any())

        mockMvc.perform(
            delete("/api/v1/contacts/1"))
                .andExpect(status().`is`(204))
    }

    @Test
    fun `test update invalid ID contact`() {
        whenever(contactService.deleteContact(any())).thenThrow(ContactException.InvalidIdFormatException())

        mockMvc.perform(
            delete("/api/v1/contacts/2"))
            .andExpect(status().`is`(400))
    }

    @Test
    fun `test delete contact already in store`() {
        whenever(contactService.deleteContact(any())).thenThrow(ContactException.ContactIsInAStoreException())

        mockMvc.perform(
            delete("/api/v1/contacts/1"))
            .andExpect(status().`is`(409))
    }
}