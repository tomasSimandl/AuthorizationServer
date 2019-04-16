package com.carsecurity.authorization.controller


import com.carsecurity.authorization.domain.dto.ClientDetailsDTO
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doThrow
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.NoSuchClientException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService
import javax.sql.DataSource

class ClientControllerTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var dataSource: DataSource

    @Mock
    private lateinit var clientDetailsService: JdbcClientDetailsService

    private lateinit var clientController: ClientController

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        clientController = ClientController(dataSource, passwordEncoder)
        clientController.setClientService(clientDetailsService)
    }

    @Test
    fun `create client success`() {

        val createClient = ClientDetailsDTO("client-id", setOf("resource-id-1", "resource-id-2"), "client-secret",
                setOf("Scope-1"), setOf("authorized-grant-type-1"), setOf("server-redirect-uri"), setOf("authority-1", "authority-2"),
                120, 600, emptyMap())

        doThrow(NoSuchClientException::class).`when`(clientDetailsService).loadClientByClientId("client-id")

        val result = clientController.createClient(createClient)

        val clientDetailsCaptor = argumentCaptor<ClientDetails>()
        verify(clientDetailsService, Times(1)).addClientDetails(clientDetailsCaptor.capture())
        assertEquals(createClient.clientId, clientDetailsCaptor.firstValue.clientId)
        assertEquals(createClient.clientSecret, clientDetailsCaptor.firstValue.clientSecret)
        assertArrayEquals(createClient.resourceIds.toTypedArray(), clientDetailsCaptor.firstValue.resourceIds.toTypedArray())
        assertArrayEquals(createClient.scope.toTypedArray(), clientDetailsCaptor.firstValue.scope.toTypedArray())
        assertArrayEquals(createClient.authorizedGrantTypes.toTypedArray(), clientDetailsCaptor.firstValue.authorizedGrantTypes.toTypedArray())
        assertArrayEquals(createClient.webServerRedirectUri.toTypedArray(), clientDetailsCaptor.firstValue.registeredRedirectUri.toTypedArray())
        assertEquals(2, clientDetailsCaptor.firstValue.authorities.size)
        assertEquals(createClient.authorities.first(), clientDetailsCaptor.firstValue.authorities.first().authority)
        assertEquals(createClient.authorities.last(), clientDetailsCaptor.firstValue.authorities.last().authority)
        assertEquals(createClient.accessTokenValidity, clientDetailsCaptor.firstValue.accessTokenValiditySeconds)
        assertEquals(createClient.refreshTokenValidity, clientDetailsCaptor.firstValue.refreshTokenValiditySeconds)
        assertEquals(createClient.additionalInformation.size, clientDetailsCaptor.firstValue.additionalInformation.size)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(createClient.clientId, result.body?.clientId)
        assertEquals(createClient.clientSecret, result.body?.clientSecret)
        assertArrayEquals(createClient.resourceIds.toTypedArray(), result.body?.resourceIds?.toTypedArray())
        assertArrayEquals(createClient.scope.toTypedArray(), result.body?.scope?.toTypedArray())
        assertArrayEquals(createClient.authorizedGrantTypes.toTypedArray(), result.body?.authorizedGrantTypes?.toTypedArray())
        assertArrayEquals(createClient.webServerRedirectUri.toTypedArray(), result.body?.webServerRedirectUri?.toTypedArray())
        assertArrayEquals(createClient.authorities.toTypedArray(), result.body?.authorities?.toTypedArray())
        assertEquals(createClient.accessTokenValidity, result.body?.accessTokenValidity)
        assertEquals(createClient.refreshTokenValidity, result.body?.refreshTokenValidity)
        assertEquals(createClient.additionalInformation.size, result.body?.additionalInformation?.size)
    }

    @Test
    fun `create client already exists`() {

        val client = BaseClientDetails("client-id", "resource-ids", "scope", "grant-types", "authorities")
        val createClient = ClientDetailsDTO("client-id", setOf("resource-id-1", "resource-id-2"), "client-secret",
                setOf("Scope-1"), setOf("authorized-grant-type-1"), setOf("server-redirect-uri"), setOf("authority-1", "authority-2"),
                120, 600, emptyMap())

        doReturn(client).`when`(clientDetailsService).loadClientByClientId("client-id")

        val result = clientController.createClient(createClient)

        assertEquals(HttpStatus.CONFLICT, result.statusCode)
        verify(clientDetailsService, Times(0)).addClientDetails(any())
    }

    @Test
    fun `delete client success`(){
        clientController.deleteClient("client-id")
        verify(clientDetailsService).removeClientDetails("client-id")
    }

    @Test
    fun `delete client not existing client`(){

        doThrow(NoSuchClientException::class).`when`(clientDetailsService).removeClientDetails("client-id")

        try {
            clientController.deleteClient("client-id")
            fail()
        } catch (e: NoSuchClientException){
            // success
        }
    }
}