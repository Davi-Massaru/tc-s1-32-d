package br.com.fiap.soat1.t32.pedidos.utils.mappers;

import br.com.fiap.soat1.t32.pedidos.domain.Pedido;
import br.com.fiap.soat1.t32.pedidos.domain.PedidoProduto;
import br.com.fiap.soat1.t32.pedidos.domain.Produto;
import br.com.fiap.soat1.t32.pedidos.driven.entities.PedidoEntity;
import br.com.fiap.soat1.t32.pedidos.driven.entities.PedidoProdutoEntity;
import br.com.fiap.soat1.t32.pedidos.driver.vo.request.ClientePedidoVo;
import br.com.fiap.soat1.t32.pedidos.driver.vo.request.PedidoVo;
import br.com.fiap.soat1.t32.pedidos.driver.vo.request.ProdutoPedidoVo;
import br.com.fiap.soat1.t32.pedidos.driver.vo.response.ListaPedidosClienteData;
import br.com.fiap.soat1.t32.pedidos.driver.vo.response.ListaPedidosData;
import br.com.fiap.soat1.t32.pedidos.driver.vo.response.ListaPedidosProdutoData;
import br.com.fiap.soat1.t32.pedidos.driver.vo.response.ListaPedidosResponse;
import br.com.fiap.soat1.t32.vendas.domain.Cliente;
import br.com.fiap.soat1.t32.vendas.driven.entities.ClienteEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoMapper {

	private Cliente toClienteDomain(ClientePedidoVo clientePedidoVo) {
		if (clientePedidoVo == null)
			return null;
		return Cliente.builder().id(clientePedidoVo.getId()).build();
	}

	private PedidoProduto toPedidoProdutoDomain(ProdutoPedidoVo produtoPedidoVo) {
		return PedidoProduto.builder()
				.quantidade(produtoPedidoVo.getQuantidade())
				.produto(toProdutoDomain(produtoPedidoVo))
				.build();
	}

	private PedidoProduto toPedidoProdutoDomain(PedidoProdutoEntity entity) {
		return PedidoProduto.builder()
				.quantidade(entity.getQuantidade())
				.produto(toProdutoDomain(entity))
				.build();
	}

	private Produto toProdutoDomain(ProdutoPedidoVo produtoPedidoVo) {
		return Produto.builder()
				.id(produtoPedidoVo.getId())
				.build();
	}

	private Produto toProdutoDomain(PedidoProdutoEntity entity) {

		var produto = entity.getProduto();

		return Produto.builder()
				.id(produto.getId())
				.descricao(produto.getDescricao())
				.preco(entity.getValorUnitario())
				.categoria(produto.getCategoria())
				.build();
	}

	private List<PedidoProduto> toListaProdutoDomain(List<ProdutoPedidoVo> listaProdutoPedidoVo) {
		return listaProdutoPedidoVo.stream().map(this::toPedidoProdutoDomain).collect(Collectors.toList());
	}

	public Pedido toDomain(PedidoVo pedidoVo) {
		return Pedido.builder().produtos(toListaProdutoDomain(pedidoVo.getProdutos()))
				.cliente(toClienteDomain(pedidoVo.getCliente())).build();
	}

	public PedidoEntity toEntity(Pedido pedido) {
		PedidoEntity pedidoEntity = new PedidoEntity();
		if (pedido.getCliente() != null) {
			pedidoEntity.setCliente(ClienteEntity.builder().id(pedido.getCliente().getId()).build());
		}
		pedidoEntity.setStatus(pedido.getStatus());
		return pedidoEntity;
	}

	private Cliente fromEntityToDomain(ClienteEntity clienteEntity) {
		if (clienteEntity == null)
			return null;
		return Cliente.builder().id(clienteEntity.getId()).nome(clienteEntity.getNome()).build();
	}

	public Pedido fromEntity(PedidoEntity pedidoEntity) {
		Pedido pedido = new Pedido();
		pedido.setId(pedidoEntity.getId());
		pedido.setCliente(fromEntityToDomain(pedidoEntity.getCliente()));
		pedido.setStatus(pedidoEntity.getStatus());
		pedido.setProdutos(pedidoEntity.getProdutos().stream()
						.map(this::toPedidoProdutoDomain)
								.collect(Collectors.toList()));
		return pedido;
	}

	public ListaPedidosData toListaVo(Pedido pedido) {
		return ListaPedidosData.builder()
				.id(pedido.getId())
				.status(pedido.getStatus())
				.produtos(toListaProdutosVo(pedido.getProdutos()))
				.cliente(toClienteVo(pedido.getCliente()))
				.build();
	}

	private ListaPedidosClienteData toClienteVo(Cliente cliente) {
		return cliente == null ? null : ListaPedidosClienteData.builder()
				.nome(cliente.getNome())
				.id(cliente.getId())
				.build();
	}

	private List<ListaPedidosProdutoData> toListaProdutosVo(List<PedidoProduto> pedidoProdutos) {
		return pedidoProdutos.stream()
				.map(produto -> ListaPedidosProdutoData.builder()
						.categoria(produto.getProduto().getCategoria())
						.id(produto.getProduto().getId())
						.descricao(produto.getProduto().getDescricao())
						.quantidade(produto.getQuantidade())
						.build())
				.collect(Collectors.toList());
	}

	public ListaPedidosResponse toListaResponse(List<Pedido> listaPedido) {
		return ListaPedidosResponse.builder()
				.pedidos(listaPedido.stream()
						.map(this::toListaVo)
						.collect(Collectors.toList())).build();
	}

}
