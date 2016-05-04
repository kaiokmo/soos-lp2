package view;

import controller.hospital.HospitalController;

import data.BancoDeDados;
import exceptions.dado.*;
import exceptions.logica.*;

public class MainFacade {

	private HospitalController hospitalController; //composicao
	
	public MainFacade() {
		hospitalController = new HospitalController();
	}
	
	public void iniciaSistema() {
		hospitalController.iniciaSistema();
	}
	
	public void fechaSistema() throws LogicaException {
		try {
			hospitalController.fechaSistema();
		}
		catch (LogicaException e) {
			throw new LogicaException("Nao foi possivel fechar o sistema. " + e.getMessage());
		}
	}
	
	public String liberaSistema(String chave, String nome, String dataNascimento) throws LogicaException {
		try {
			return hospitalController.liberaSistema(chave, nome, dataNascimento);
		}
		catch (LogicaException | DadoInvalidoException e) {
			throw new LogicaException("Erro ao liberar o sistema. " + e.getMessage());
		}
	}
	
	public void login(String matricula, String senha) throws LogicaException {
		try {
			hospitalController.login(matricula, senha);
		} catch (LogicaException | DadoInvalidoException e) {
			throw new LogicaException("Nao foi possivel realizar o login. " + e.getMessage());
		}
	}
	
	public void logout() throws LogicaException {
		try {
			hospitalController.logout();
		} catch (LogicaException e) {
			throw new LogicaException("Nao foi possivel realizar o logout. " + e.getMessage());
		}
	}
	
	public String getInfoFuncionario(String matricula, String atributo) throws LogicaException {
		try {
			return hospitalController.getInfoFuncionario(matricula, atributo);
		}
		catch (LogicaException | DadoInvalidoException e) {
			throw new LogicaException("Erro na consulta de funcionario. " + e.getMessage());
		}
	}
	
	public String cadastraFuncionario(String nome, String cargo, String dataNascimento) throws LogicaException {
		try {
			return hospitalController.cadastraFuncionario(nome, cargo, dataNascimento);
		} catch (LogicaException | DadoInvalidoException e) {
			throw new LogicaException("Erro no cadastro de funcionario. " + e.getMessage());
		}
	}

}