package supercompras;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RepositorioEmMemoria implements PedidoRepositorio {

    public final List<Pedido> salvos = new ArrayList<>();
    private final Set<String> compradoresQueFalham = new HashSet<>();

    public RepositorioEmMemoria falhandoParaComprador(String compradorId) {
        compradoresQueFalham.add(compradorId);
        return this;
    }

    @Override
    public void salvar(Pedido pedido) {
        if (compradoresQueFalham.contains(pedido.getCompradorId())) {
            throw new RuntimeException("falha simulada ao salvar");
        }
        salvos.add(pedido);
    }

    @Override
    public List<Pedido> buscarPorComprador(String compradorId) {
        List<Pedido> encontrados = new ArrayList<>();
        for (Pedido p : salvos) {
            if (p.getCompradorId().equals(compradorId)) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }
}
