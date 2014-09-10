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
			Resultado resultado = new Resultado();
			resultado.setImpostoDevido(this.calcularImpostoDevido(titular));
			return resultado;
		}	
		public int calcularFaixa(double totalRendimentos){
			if(totalRendimentos < 19645.33){
				return 1;
			}
			else if (totalRendimentos >= 19645.33 && totalRendimentos < 29442.01) {
				return 2;
			}
			else if(totalRendimentos >= 29442.01 && totalRendimentos < 39256.57){
				return 3;
			}
			else if(totalRendimentos >= 39256.57 && totalRendimentos < 49051.9){
				return 4;
			}
			else if(totalRendimentos > 49051.80){
				return 5;
			}
			return 0;
		}
		public double calcularImpostoDevido(Titular titular) {
			double aliquota = 0, parcelaDeducao = 0, totalRendimentos = 0, deducaoPorDependente = 0;
			for (FontePagadora fp : this.listarFontes(titular)) {
				totalRendimentos += fp.getRendimentoRecebidos();
			}
			deducaoPorDependente = 1974.72 * this.listarDependentes(titular).size();
			totalRendimentos -= deducaoPorDependente;
			switch (this.calcularFaixa(totalRendimentos)) {
				case 1:
					aliquota = 0;
					parcelaDeducao = 0;
					break;
				case 2:
					aliquota = 7.5 / 100;
					parcelaDeducao = 1473.36;
					break;
				case 3:
					aliquota = 15.0 / 100;
					parcelaDeducao = 3681.60;
					break;
				case 4:
					aliquota = 22.5 / 100;
					parcelaDeducao = 6625.80;
					break;
				case 5:
					aliquota = 27.5 / 100;
					parcelaDeducao = 9078.36;
					break;
				default:
					aliquota = 0;
					parcelaDeducao = 0;
					break;
		}

		return (totalRendimentos * aliquota) - parcelaDeducao;
		}
}
