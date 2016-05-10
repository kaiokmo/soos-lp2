package model.usuarios;

import java.util.HashMap;

import data.BancoDeDados;
import exceptions.dado.DadoInvalidoException;
import exceptions.dado.NullStringException;
import exceptions.logica.AtributoInvalidoException;
import exceptions.logica.ChaveIncorretaException;
import exceptions.logica.LogicaException;
import exceptions.logica.LoginException;
import exceptions.logica.LogoutException;
import exceptions.logica.ObjetoInexistenteException;
import exceptions.logica.PadraoException;
import exceptions.logica.PermissaoException;
import exceptions.logica.SenhaIncorretaException;
import exceptions.logica.SistemaException;
import exceptions.logica.StringVaziaException;
import factory.funcionarios.FuncionarioFactory;
import validacao.dados.ValidaMatricula;

public class GerenciadorFuncionarios {

	private static final String CHAVE_SISTEMA = "c041ebf8";
	
	private HashMap<String, Funcionario> funcionarios;
	private FuncionarioFactory funcionarioFactory;	
	
	private Funcionario usuarioLogado;
	private BancoDeDados bancoDeDados;
	
	public GerenciadorFuncionarios() {
		funcionarios = new HashMap<String, Funcionario>();
		funcionarioFactory = new FuncionarioFactory();
		
		bancoDeDados = BancoDeDados.getInstance();
	}
	
	public void initGerenciadorFuncionarios() {
		
	}
	
	public void fecharGerenciadorFuncionarios() throws LogicaException {
		if (isUsuarioLogado()) {
			throw new LogicaException("Um funcionario ainda esta logado: " + usuarioLogado.getNome() + ".");
		}
	}
	
	/**
	 * Libera o sistema, criando um usuário com privilégios de Diretor e
	 * retornando o seu numero de matrícula
	 * 
	 * @param chave
	 *            Chave para liberar o sistema
	 * @param nome
	 *            Nome do diretor
	 * @param dataNascimento
	 *            Data de nascimento do diretor conforme o padrão dd/MM/yyyy
	 * @return Matrícula do diretor
	 * @throws LogicaException
	 * @throws DadoInvalidoException
	 */
	public String liberaSistema(String chave, String nome, String dataNascimento)
			throws LogicaException, DadoInvalidoException {
		if (chave == null) {
			throw new NullStringException("Chave nao pode ser nulo.");
		}
		if (nome == null) {
			throw new NullStringException("Nome nao pode ser nulo.");
		}
		if (dataNascimento == null) {
			throw new NullStringException("Data nao pode ser nulo.");
		}
		if (bancoDeDados.isSistemaLiberado()) {
			throw new LogicaException("Sistema liberado anteriormente.");
		}
		if (!chave.equals(CHAVE_SISTEMA)) {
			throw new ChaveIncorretaException("Chave invalida.");
		}
		bancoDeDados.setSistemaLiberado(true);
		
		Diretor diretor = (Diretor) funcionarioFactory.criaFuncionario(nome, "Diretor Geral", dataNascimento);
		funcionarios.put(diretor.getMatricula(), diretor);

		return diretor.getMatricula();
	}

	/**
	 * Realiza o login de um usuário
	 * 
	 * @param matricula
	 *            Matrícula do usuário
	 * @param senha
	 *            Senha do usuário
	 * @throws LogicaException
	 * @throws NullStringException
	 */
	public void login(String matricula, String senha) throws LogicaException, NullStringException {
		if (!bancoDeDados.isSistemaLiberado()) {
			throw new SistemaException("Sistema nao liberado.");
		} else if (isUsuarioLogado()) {
			throw new LoginException("Um funcionario ainda esta logado: " + usuarioLogado.getNome() + ".");
		} else if (matricula == null) {
			throw new NullStringException("Matricula nao pode ser nulo.");
		} else if (senha == null) {
			throw new NullStringException("Senha nao pode ser nulo.");
		}

		Funcionario funcionario = getFuncionarioPorMatricula(matricula);

		if (!funcionario.getSenha().equals(senha)) {
			throw new SenhaIncorretaException("Senha incorreta.");
		}

		usuarioLogado = funcionario;
	}

	/**
	 * Realiza o logout de um usuário, e joga um erro caso não tenha usuários
	 * logados
	 * 
	 * @throws LogicaException
	 */
	public void logout() throws LogoutException {
		if (!isUsuarioLogado()) {
			throw new LogoutException("Nao ha um funcionario logado.");
		}
		usuarioLogado = null;
	}
	
	/**
	 * Verifica se tem algum usuário logado
	 * 
	 * @return Boleano indicando se tem algum usuário logado
	 */
	public boolean isUsuarioLogado() {
		return usuarioLogado != null;
	}
	
	/**
	 * Retorna um funcionário dado seu numero de matrícula, e joga um erro
	 * caso não exista um funcionário com aquela matrícula
	 * 
	 * @param matricula
	 *            Matrícula do usuário
	 * @return Instância de um objeto Funcionario
	 * @throws ObjetoInexistenteException
	 */
	public Funcionario getFuncionarioPorMatricula(String matricula) throws ObjetoInexistenteException {
		Funcionario funcionario = funcionarios.get(matricula);

		if (funcionario == null) {
			throw new ObjetoInexistenteException("Funcionario nao cadastrado.");
		}

		return funcionario;
	}

	/**
	 * Cadastra um funcionário, retornando seu número de matrícula
	 * 
	 * @param nome
	 *            Nome do funcionário
	 * @param cargo
	 *            Cargo do funcionário
	 * @param dataNascimento
	 *            Data de nascimento do funcionário
	 * @return Matrícula do funcionário criado
	 * @throws LogicaException
	 * @throws DadoInvalidoException
	 */
	public String cadastraFuncionario(String nome, String cargo, String dataNascimento)
			throws LogicaException, DadoInvalidoException {
		if (nome == null) {
			throw new NullStringException("Nome do funcionario nao pode ser nulo.");
		} else if (nome.trim().isEmpty()) {
			throw new StringVaziaException("Nome do funcionario nao pode ser vazio.");
		}
		if (cargo == null) {
			throw new NullStringException("Nome do cargo nao pode ser nulo.");
		} else if (cargo.trim().isEmpty()) {
			throw new StringVaziaException("Nome do cargo nao pode ser vazio.");
		}

		if (!bancoDeDados.isSistemaLiberado()) {
			throw new SistemaException("O sistema esta bloqueado.");
		} else if (!isUsuarioLogado()) {
			throw new SistemaException("Usuario nao esta logado.");
		} else if (!usuarioLogado.temPermissao(PermissaoFuncionario.criacaoUsuarios)) {
			throw new PermissaoException(
					"O funcionario " + usuarioLogado.getNome() + " nao tem permissao para cadastrar funcionarios.");
		} else if (cargo.equalsIgnoreCase("Diretor Geral")) {
			throw new PermissaoException("Nao eh possivel criar mais de um Diretor Geral.");
		}

		Funcionario funcionario = funcionarioFactory.criaFuncionario(nome, cargo, dataNascimento);
		funcionarios.put(funcionario.getMatricula(), funcionario);

		return funcionario.getMatricula();
	}

	/**
	 * Pega o atributo do funcionário requisitado
	 * 
	 * @param matricula
	 *            Matrícula do funcionário
	 * @param atributo
	 *            Atributo a ser requisitado
	 * @return Valor do atributo do funcionário
	 * @throws LogicaException
	 * @throws NullStringException
	 */
	public String getInfoFuncionario(String matricula, String atributo) throws LogicaException, NullStringException {
		if (!isUsuarioLogado()) {
			throw new LogicaException("Usuario nao esta logado.");
		} else if (matricula == null) {
			throw new NullStringException("Matricula nao pode ser nulo.");
		} else if (atributo == null) {
			throw new NullStringException("Atributo nao pode ser nulo.");
		} else if (!ValidaMatricula.validar(matricula)) {
			throw new PadraoException("A matricula nao segue o padrao.");
		}

		Funcionario funcionario = getFuncionarioPorMatricula(matricula);

		String attr = null;

		switch (atributo) {

		case "Nome":
			attr = funcionario.getNome();
			break;
		case "Cargo":
			attr = funcionario.getCargo();
			break;
		case "Data":
			attr = funcionario.getDataNascimento();
			break;
		case "Senha":
			if (usuarioLogado.getMatricula().equals(funcionario.getMatricula())) {
				attr = funcionario.getSenha();
			} else {
				throw new PermissaoException("A senha do funcionario eh protegida.");
			}
			break;
		default:
			throw new AtributoInvalidoException("Atributo invalido.");
		}

		return attr;
	}
	
	public void excluiFuncionario(String matricula, String senhaDiretor) throws DadoInvalidoException, LogicaException {
		if (matricula == null) {
			throw new NullStringException("Matricula nao pode ser nulo.");
		} else if (senhaDiretor == null) {
			throw new NullStringException("Senha nao pode ser nula.");
		} else if (matricula.trim().isEmpty()) {
			throw new StringVaziaException("Matricula nao pode ser vazia.");
		} else if (senhaDiretor.trim().isEmpty()) {
			throw new StringVaziaException("Senha nao pode ser vazia.");
		} else if (!ValidaMatricula.validar(matricula)) {
			throw new PadraoException("A matricula nao segue o padrao.");
		}
		
		Funcionario funcionario = getFuncionarioPorMatricula(matricula);
		
		if (!getFuncionarioPorMatricula("12016001").getSenha().equals(senhaDiretor)) {
			throw new SenhaIncorretaException("Senha invalida.");
		}
		
		funcionarios.remove(funcionario.getMatricula());
	}
}
