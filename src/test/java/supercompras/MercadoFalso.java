package supercompras;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class MercadoFalso implements Mercado {

    private final String nome;
    private Map<String, BigDecimal> precos = new HashMap<>();
    private BigDecimal frete = BigDecimal.ZERO;
    private String protocolo = "PROTO-1";

    private Throwable falhaAoCotar;
    private Throwable falhaNoFrete;
    private Throwable falhaAoComprar;

    public Map<String, Integer> loteRecebido;
    public Map<String, BigDecimal> precosRecebidosNaCompra;
    public int vezesQueComprou = 0;

    public MercadoFalso(String nome) {
        this.nome = nome;
    }

    public MercadoFalso comPreco(String sku, String valor) {
        precos.put(sku, new BigDecimal(valor));
        return this;
    }

    public MercadoFalso comPrecos(Map<String, BigDecimal> precos) {
        this.precos = precos;
        return this;
    }

    public MercadoFalso comFrete(String valor) {
        this.frete = valor == null ? null : new BigDecimal(valor);
        return this;
    }

    public MercadoFalso comProtocolo(String protocolo) {
        this.protocolo = protocolo;
        return this;
    }

    public MercadoFalso falhandoAoCotar(Throwable falha) {
        this.falhaAoCotar = falha;
        return this;
    }

    public MercadoFalso falhandoNoFrete(Throwable falha) {
        this.falhaNoFrete = falha;
        return this;
    }

    public MercadoFalso falhandoAoComprar(Throwable falha) {
        this.falhaAoComprar = falha;
        return this;
    }

    @Override
    public String nome() {
        return nome;
    }

    @Override
    public Map<String, BigDecimal> cotarPrecosUnitarios(Map<String, Integer> itens)
            throws MercadoIndisponivelException {
        loteRecebido = itens;
        lancar(falhaAoCotar);
        return precos;
    }

    @Override
    public BigDecimal frete(Map<String, Integer> itens) throws MercadoIndisponivelException {
        lancar(falhaNoFrete);
        return frete;
    }

    @Override
    public String comprar(Map<String, Integer> itens, Map<String, BigDecimal> precosUnitarios)
            throws MercadoIndisponivelException {
        vezesQueComprou++;
        precosRecebidosNaCompra = precosUnitarios;
        lancar(falhaAoComprar);
        return protocolo;
    }

    private static void lancar(Throwable falha) throws MercadoIndisponivelException {
        if (falha instanceof MercadoIndisponivelException e) throw e;
        if (falha instanceof RuntimeException e) throw e;
        if (falha instanceof Error e) throw e;
    }
}
