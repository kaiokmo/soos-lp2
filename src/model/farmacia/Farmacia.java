package model.farmacia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import exceptions.dado.DadoInvalidoException;
import exceptions.logica.CategoriaInexistenteException;
import exceptions.logica.CategoriaInvalidaException;
import exceptions.logica.ComparacaoInvalidaException;
import exceptions.logica.LogicaException;
import exceptions.logica.ObjetoInexistenteException;
import exceptions.logica.OperacaoInvalidaException;
import factory.medicamentos.MedicamentoFactory;
import validacao.medicamentos.ValidacaoMedicamentos;

public class Farmacia {

	private MedicamentoFactory medicamentoFactory;

	private HashMap<String, Medicamento> medicamentos;

	public Farmacia() {
		medicamentoFactory = new MedicamentoFactory();
		medicamentos = new HashMap<String, Medicamento>();
	}

	public Medicamento getMedicamentoPeloNome(String nome) throws ObjetoInexistenteException {
		Medicamento medicamento = medicamentos.get(nome);

		ValidacaoMedicamentos.validaObjetoMedicamento(medicamento);

		return medicamento;
	}

	public String getMedicamentosPelaCategoria(String categoria)
			throws CategoriaInexistenteException, CategoriaInvalidaException {

		if (!"analgesico,antibiotico,antiemetico,antiinflamatorio,antitermico,hormonal".contains(categoria)) {
			throw new CategoriaInvalidaException("Categoria invalida.");
		}

		ArrayList<Medicamento> arrayCategorias = new ArrayList<Medicamento>();

		for (Map.Entry<String, Medicamento> entry : medicamentos.entrySet()) {

			if (entry.getValue().getInfoMedicamento("categorias").contains(categoria)) {
				arrayCategorias.add(entry.getValue());
			}

		}

		if (arrayCategorias.isEmpty()) {
			throw new CategoriaInexistenteException("Nao ha remedios cadastrados nessa categoria.");
		}

		Collections.sort(arrayCategorias);
		String saida = "";

		for (int i = 0; i < arrayCategorias.size(); i++) {
			if (i == arrayCategorias.size() - 1) {
				saida += arrayCategorias.get(i).getInfoMedicamento("nome");
			} else {
				saida += arrayCategorias.get(i).getInfoMedicamento("nome") + ",";
			}
		}

		return saida;

	}

	public String cadastraMedicamento(String nome, String tipo, double preco, int quantidade, String categorias)
			throws DadoInvalidoException, LogicaException {
		Medicamento medicamento = medicamentoFactory.criaMedicamento(nome, tipo, preco, quantidade, categorias);
		medicamentos.put(nome, medicamento);

		return nome;
	}

	public String getInfoMedicamento(String requisicao, String nome) throws ObjetoInexistenteException {
		Medicamento medicamento = getMedicamentoPeloNome(nome);

		return medicamento.getInfoMedicamento(requisicao);
	}

	public void atualizaMedicamento(String nome, String atributo, String novoValor)
			throws ObjetoInexistenteException, OperacaoInvalidaException {
		getMedicamentoPeloNome(nome).atualizaMedicamento(atributo, novoValor);
	}

	public String consultaMedCategoria(String categoria) throws LogicaException {
		try {
			return getMedicamentosPelaCategoria(categoria);
		} catch (CategoriaInexistenteException | CategoriaInvalidaException e) {
			throw new LogicaException("Erro na consulta de medicamentos. " + e.getMessage());
		}
	}

	public String consultaMedNome(String nome) throws ObjetoInexistenteException {
		return getMedicamentoPeloNome(nome).toString();
	}

	public String getEstoqueFarmacia(String tipoOrdenacao) throws ComparacaoInvalidaException {

		if (!tipoOrdenacao.equals("preco") && !tipoOrdenacao.equals("alfabetica")) {
			throw new ComparacaoInvalidaException("Tipo de ordenacao invalida.");
		}

		ArrayList<Medicamento> arrayMedicamentos = new ArrayList<Medicamento>();

		for (Map.Entry<String, Medicamento> entry : medicamentos.entrySet()) {

			arrayMedicamentos.add(entry.getValue());

		}

		Comparador comparador = new Comparador();

		if (tipoOrdenacao.equals("preco")) {
			Collections.sort(arrayMedicamentos);
		} else if (tipoOrdenacao.equals("alfabetica")) {
			Collections.sort(arrayMedicamentos, comparador);
		}
		String saida = "";

		for (int i = 0; i < arrayMedicamentos.size(); i++) {
			if (i == arrayMedicamentos.size() - 1) {
				saida += arrayMedicamentos.get(i).getInfoMedicamento("nome");
			} else {
				saida += arrayMedicamentos.get(i).getInfoMedicamento("nome") + ",";
			}
		}

		return saida;

	}

}