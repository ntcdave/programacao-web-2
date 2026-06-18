package com.althabeauty.api;

import com.althabeauty.api.model.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteEntityTest {

    @Test
    void deveCriarClienteComGettersESetters() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Maria Silva");
        cliente.setTelefone("11999999999");
        cliente.setEmail("maria@email.com");

        assertEquals(1L, cliente.getId());
        assertEquals("Maria Silva", cliente.getNome());
        assertEquals("11999999999", cliente.getTelefone());
        assertEquals("maria@email.com", cliente.getEmail());
    }

    @Test
    void deveTestarEqualsEHashCode() {
        Cliente cliente1 = new Cliente();
        cliente1.setId(1L);
        Cliente cliente2 = new Cliente();
        cliente2.setId(1L);

        assertEquals(cliente1, cliente2);
        assertEquals(cliente1.hashCode(), cliente2.hashCode());
    }
}
