package br.ufcg.ppgcc.compor.ir.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import br.ufcg.ppgcc.compor.ir.FachadaExperimento;
import br.ufcg.ppgcc.compor.ir.Titular;
import br.ufcg.ppgcc.compor.ir.ExcecaoImpostoDeRenda;
import br.ufcg.ppgcc.compor.ir.FontePagadora;
import br.ufcg.ppgcc.compor.ir.Dependente;
import br.ufcg.ppgcc.compor.ir.Resultado;

public class ImpostoDeRenda implements FachadaExperimento {

	private Map<Titular, List<FontePagadora>> historicoTitularFonte = new LinkedHashMap<Titular, List<FontePagadora>>();
	private Map<Titular, List<Dependente>> dependentes = new LinkedHashMap<Titular, List<Dependente>>();
	
		public void criarNovoTitular(Titular titular) {
			if(titular.getNome() == null){
				throw new ExcecaoImpostoDeRenda("O campo nome é obrigatório");
			}
			if(titular.getCpf() == null){
				throw new ExcecaoImpostoDeRenda("O campo CPF é obrigatório");
			}
			if (!titular.getCpf().matches("\\d{3}.\\d{3}.\\d{3}-\\d{2}")) {
				throw new ExcecaoImpostoDeRenda("O campo CPF está inválido");
			}
			historicoTitularFonte.put(titular, new ArrayList<FontePagadora>());
			dependentes.put(titular, new ArrayList<Dependente>());
		}
		public List<Titular> listarTitulares() {
			return new ArrayList<Titular>(historicoTitularFonte.keySet());
		}
		public void criarFontePagadora(Titular titular, FontePagadora fonte) {
			if (fonte.getNome() == null) {
				throw new ExcecaoImpostoDeRenda("O campo nome é obrigatório");
			}
			if (fonte.getRendimentoRecebidos() == 0.0) {
				throw new ExcecaoImpostoDeRenda(
					"O campo rendimentos recebidos é obrigatório");
			} else if (fonte.getRendimentoRecebidos() < 0.0) {
				throw new ExcecaoImpostoDeRenda(
						"O campo rendimentos recebidos deve ser maior que zero");
			}
				if (fonte.getCpfCnpj() == null) {
				throw new ExcecaoImpostoDeRenda("O campo CPF/CNPJ é obrigatório");
			}
			else if (!fonte.getCpfCnpj().matches("[\\d]{2}\\.[\\d]{3}\\.[\\d]{3}\\/[\\d]{4}\\-[\\d]{2}")) {
				throw new ExcecaoImpostoDeRenda("O campo CPF/CNPJ é inválido");
			}
			if (historicoTitularFonte.containsKey(titular)) {
				List<FontePagadora> listaDeFontesDoTitular = historicoTitularFonte.get(titular);
				listaDeFontesDoTitular.add(fonte);
				historicoTitularFonte.put(titular, listaDeFontesDoTitular);
			} else {
				throw new ExcecaoImpostoDeRenda("Titular não cadastrado");
			}
		}
		public List<FontePagadora> listarFontes(Titular titular) {
			return historicoTitularFonte.get(titular);
		}
		public void criarDependente(Titular titular, Dependente dependente) {
			if (dependente.getCpf() == null) {
				throw new ExcecaoImpostoDeRenda("O campo CPF é obrigatório");
				}
			if (dependente.getNome() == null) {
				throw new ExcecaoImpostoDeRenda("O campo nome é obrigatório");
			}
			if (dependente.getTipo() == 0) {
				throw new ExcecaoImpostoDeRenda("O campo tipo é obrigatório");
			}
			if (dependente.getTipo() < 0) {
				throw new ExcecaoImpostoDeRenda("O campo tipo é inválido");
			}
			if (!dependente.getCpf().matches("\\d{3}.\\d{3}.\\d{3}-\\d{2}")) {
				throw new ExcecaoImpostoDeRenda("O campo CPF é inválido");
			}
			if (dependentes.containsKey(titular)) {
				ArrayList<Dependente> dependentesDoTitular = (ArrayList<Dependente>) dependentes.get(titular);
				dependentesDoTitular.add(dependente);
				dependentes.put(titular, dependentesDoTitular);
			}
			else {
				throw new ExcecaoImpostoDeRenda("Titular não cadastrado");
			}
		}
		public List<Dependente> listarDependentes(Titular titular) {
			return new ArrayList<Dependente>(dependentes.get(titular));
		}
		public Resultado declaracaoCompleta(Titular titular) {
			double aliquota = 0,parcelaDeducao=0,impostoDevido=0;
			Resultado resultado = new Resultado();
			double somatorioRendimentos = 0;
			for (FontePagadora fp : listarFontes(titular)) {
				somatorioRendimentos += fp.getRendimentoRecebidos();
			}
			if(somatorioRendimentos < 19645.33){
				aliquota = 0;
				parcelaDeducao = 0;
				}
			else if (somatorioRendimentos >= 19645.33 && somatorioRendimentos <= 29442.0) {
				aliquota = 7.5/100;
				parcelaDeducao = 1473.4;
			}
			else if(somatorioRendimentos >= 29442.01 && somatorioRendimentos <= 39256.56){
				aliquota = 15.0/100;
				parcelaDeducao = 3681.55;
			}
			else if(somatorioRendimentos >= 39256.57 && somatorioRendimentos <= 49051.8){
				aliquota = 22.5/100;
				parcelaDeducao = 6625.79;
			}
			else if(somatorioRendimentos > 49051.80){
				aliquota = 27.5/100;
				parcelaDeducao = 9078.38;
			}	
			impostoDevido = (somatorioRendimentos * aliquota) - parcelaDeducao;
			resultado.setImpostoDevido(impostoDevido);
			return resultado;
		}
}
