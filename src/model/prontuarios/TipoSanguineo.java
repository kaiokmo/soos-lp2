package model.prontuarios;

/**
 * Enum do tipo sanguineo do paciente
 */
public enum TipoSanguineo {
	A_POSITIVO("A+"), A_NEGATIVO("A-"), B_POSITIVO("B+"), B_NEGATIVO("B-"), AB_POSITIVO("AB+"), AB_NEGATIVO(
			"AB-"), O_POSITIVO("O+"), O_NEGATIVO("O-");

	private String tipoSanguineo;

	/**
	 * Construtor do enum
	 * 
	 * @param tipoSanguineo
	 *            - tipo sanguineo do paciente
	 */
	private TipoSanguineo(String tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}

	/**
	 * Retorna a string do tipo sanguineo do paciente
	 * 
	 * @param tipo
	 *            - tipo sanguineo do enum
	 * @return Tipo sanguineo
	 */
	public static TipoSanguineo getTipoSanguineo(String tipo) {
		TipoSanguineo[] tipos = TipoSanguineo.values();

		for (TipoSanguineo tipoSanguineo : tipos) {
			if (tipoSanguineo.toString().equals(tipo)) {
				return tipoSanguineo;
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return this.tipoSanguineo;
	}
}
