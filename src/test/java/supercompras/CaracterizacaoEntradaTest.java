package supercompras;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Testes de caracterização de SistemaCompras.receberLista.
 *
 * Registram o comportamento ATUAL do legado, mesmo quando ele contraria RN-01 e RN-02.
 * Observam apenas o comportamento público (fecharRodada, getPedidos e o que o
 * MercadoFalso recebeu), nunca campos internos, para sobreviver à refatoração.
 */
public class CaracterizacaoEntradaTest {

    private MercadoFalso mercado;
    private SistemaCompras sistema;

    @Before
    public void prepara() {
        mercado = new MercadoFalso("A").comPreco("LEI-0001", "1.00");
        sistema = new SistemaCompras(List.of(mercado), new RepositorioEmMemoria());
    }

    private static Map<String, Integer> lista(String sku, Integer quantidade) {
        Map<String, Integer> itens = new HashMap<>();
        itens.put(sku, quantidade);
        return itens;
    }

    // 1
    @Test
    public void segundaListaDoMesmoCompradorSubstituiAPrimeira() {
        sistema.receberLista("ana", lista("LEI-0001", 1));
        sistema.receberLista("ana", lista("LEI-0001", 5));
        sistema.fecharRodada();

        assertEquals(Integer.valueOf(5), mercado.loteRecebido.get("LEI-0001"));
    }

    // 2
    @Test
    public void listaNulaEhIgnoradaSemErro() {
        sistema.receberLista("ana", null);
        sistema.receberLista("bia", lista("LEI-0001", 2));
        ResultadoRodada resultado = sistema.fecharRodada();

        List<Pedido> pedidos = resultado.getPedidos();
        assertEquals(1, pedidos.size());
        assertEquals("bia", pedidos.get(0).getCompradorId());
    }

    // 3
    @Test
    public void aceitaIdentificadorDeCompradorInvalido() {
        sistema.receberLista("ANA COM ESPAÇO", lista("LEI-0001", 2));
        ResultadoRodada resultado = sistema.fecharRodada();

        assertEquals("ANA COM ESPAÇO", resultado.getPedidos().get(0).getCompradorId());
    }
}
