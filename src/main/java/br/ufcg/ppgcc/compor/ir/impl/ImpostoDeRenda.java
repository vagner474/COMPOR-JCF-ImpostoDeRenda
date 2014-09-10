package br.ufcg.ppgcc.compor.ir.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import br.ufcg.ppgcc.compor.ir.FachadaExperimento;
import br.ufcg.ppgcc.compor.ir.Titular;
import br.ufcg.ppgcc.compor.ir.ExcecaoImpostoDeRenda;
import br.ufcg.ppgcc.compor.ir.FontePagadora;

public class ImpostoDeRenda implements FachadaExperimento {

	private Map<Titular, List<FontePagadora>> historicoTitularFonte = new LinkedHashMap<Titular, List<FontePagadora>>();
	
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
}
