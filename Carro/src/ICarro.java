public interface ICarro {
    double acaoAbastecer(double precoLitro, double valorAbastecimento) throws NotNegative;

    void acaoAcelerar(double segundos) throws NotNegative;

    void acaoFrear(double segundos) throws NotNegative;

    void acaoMaterVelocidade(double segundos) throws NotNegative;

    double getVelocidadeAtual();

    double getVolumeAtualDeCombustivel();
}
