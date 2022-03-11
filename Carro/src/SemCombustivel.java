public class SemCombustivel extends NotNegative {
    public SemCombustivel() {
        super("Sem o combustível!\n");
    }
    public SemCombustivel(Carro carro) {
        super("Sem o combustível!\n");
        System.out.printf("\n\nAcabou o combustível no km: %.5f\n", carro.getQuilometragemX());
        carro.resumo();
    }
}
