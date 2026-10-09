package com.baozistore.baozistore.controller;

import com.baozistore.baozistore.model.Cliente;
import com.baozistore.baozistore.model.Pedido;
import com.baozistore.baozistore.model.Produto;
import com.baozistore.baozistore.repository.ClienteRepository;
import com.baozistore.baozistore.repository.PedidoRepository;
import com.baozistore.baozistore.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST - Registrar Pedido (com abatimento automático do estoque)
    @PostMapping
    public ResponseEntity<?> registrarPedido(@RequestBody PedidoRequest request) {
        if (request.getClienteId() == null || request.getProdutoId() == null || request.getQuantidade() == null) {
            return ResponseEntity.badRequest().body("Os campos clienteId, produtoId e quantidade são obrigatórios.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(request.getClienteId());
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado com o ID: " + request.getClienteId());
        }

        Optional<Produto> produtoOpt = produtoRepository.findById(request.getProdutoId());
        if (produtoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado com o ID: " + request.getProdutoId());
        }

        Produto produto = produtoOpt.get();

        // Validação do Estoque
        if (produto.getEstoque() < request.getQuantidade()) {
            return ResponseEntity.badRequest().body("Estoque insuficiente! Estoque atual: " + produto.getEstoque());
        }

        // Subtrai a quantidade informada do estoque do produto
        produto.setEstoque(produto.getEstoque() - request.getQuantidade());
        produtoRepository.save(produto);

        // Cria e salva o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(clienteOpt.get());
        pedido.setProduto(produto);
        pedido.setQuantidade(request.getQuantidade());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoSalvo);
    }

    // GET - Listar Todos os Pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> listar() {
        return ResponseEntity.ok(pedidoRepository.findAll());
    }

    // GET por ID - Buscar Pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Deletar Pedido por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO para recepção da requisição JSON do Pedido
    public static class PedidoRequest {
        private Long clienteId;
        private Long produtoId;
        private Integer quantidade;

        public PedidoRequest() {}

        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    }
}