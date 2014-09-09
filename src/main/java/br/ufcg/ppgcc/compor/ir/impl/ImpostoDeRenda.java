package br.ufcg.ppgcc.compor.ir.impl;

import java.util.ArrayList;
import java.util.List;
import br.ufcg.ppgcc.compor.ir.FachadaExperimento;
import br.ufcg.ppgcc.compor.ir.Titular;
import br.ufcg.ppgcc.compor.ir.ExcecaoImpostoDeRenda;

public class ImpostoDeRenda implements FachadaExperimento {

	private List<Titular> titulares = new ArrayList<Titular>();
	
		public void criarNovoTitular(Titular titular) {
			if(titular.getNome() == null){
				throw new ExcecaoImpostoDeRenda("O campo Nome é obrigatório");
			}
			titulares.add(titular);	
		}
		public List<Titular> listarTitulares() {
			return this.titulares;
		}
}
