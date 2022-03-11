import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Carro implements ICarro {
    public static double INTERVALO_DE_TEMPO = 1;
    public static double SLEEP_SHOW = 0.005;
    public static double SLEEP = 100;
    // variaveis pedidas pela atividade
    private String nomeModedo;
    private BigDecimal quilometragem;
    private double volumeDeCombustivel = 0;
    private int capacidadeDoTanque;
    private double velocidadeMaxima;
    private double velocidadeAtual = 0;
    private double aceleracaoMedia;
    private double consumoMedio; // (km/L)

    // variaveis de apoio
    private Movimento movimento = Movimento.CONSTANTE;
    private double v = 0, v0 = 0, t = 0, a = 0;
    private BigDecimal _s0, _Δs, _s;
    private double precoLitro = 7;
    private double topSpeed = 0;
    private double tempo = 0;
    private double consumoMedioInstante;
    private double volumeDeCombustivelInicial;

    public Carro(String nomeModedo, int capacidadeDoTanque, double consumoMedio, double velocidadeMaxima,
            double aceleracaoMedia) {
        this.nomeModedo = nomeModedo;
        this.capacidadeDoTanque = capacidadeDoTanque;
        this.consumoMedio = consumoMedio;
        this.velocidadeMaxima = velocidadeMaxima;
        this.aceleracaoMedia = aceleracaoMedia;
        this.a = aceleracaoMedia;

        this.quilometragem = new BigDecimal(0);
        this._s0 = new BigDecimal(0);
        this._s = new BigDecimal(0);
        this._Δs = new BigDecimal(0);
    }

    @Override
    public double acaoAbastecer(double precoLitro, double valorAbastecimento) throws NotNegative {
        if (valorAbastecimento <= 0 || precoLitro <= 0)
            throw new NotNegative("Erro: Valor informado menor ou igual a zero!");
        double troco = valorAbastecimento;
        this.precoLitro = precoLitro;
        BigDecimal litros = BigDecimal.valueOf(valorAbastecimento / precoLitro);
        // não faz nada e devolve o troco!
        int sleep = 1;

        System.out.printf("\n%22s: %.3fL\n", "Volume de Combustível", this.volumeDeCombustivel);
        System.out.printf("%22s: %.3fL\n", "Litros", litros);
        if (this.capacidadeDoTanque != this.volumeDeCombustivel) {
            // cabe tudo
            if (this.capacidadeDoTanque >= this.volumeDeCombustivel + litros.doubleValue()) {
                this.volumeDeCombustivel += litros.doubleValue();
                troco = 0;
            } else {
                // excesso de combustivel
                litros = BigDecimal.valueOf(this.volumeDeCombustivel + litros.doubleValue() - this.capacidadeDoTanque);
                // calc troco
                troco = litros.doubleValue() * precoLitro;
                System.out.printf("%22s: R$%.2f\n", "Troco", troco);
                // tanque cheio
                this.volumeDeCombustivel = this.capacidadeDoTanque;
            }
        }
        System.out.printf("%22s: %.3fL\n", "Nivel Atual", this.volumeDeCombustivel);
        this.volumeDeCombustivelInicial = this.volumeDeCombustivel;

        sleep(5, sleep, "!");
        return troco;
    }

    @Override
    public void acaoAcelerar(double segundos) throws NotNegative {
        if (segundos <= 0)
            throw new NotNegative("Segundos não podem ser menor ou igual a zero");
        movimento = Movimento.ACELERADO;
        t = INTERVALO_DE_TEMPO;
        double max = segundos;
        double val = 0;
        while (segundos > 0) {
            segundos = atualize(segundos);
            val += t;
            show(val, max);
        }
    }

    @Override
    public void acaoFrear(double segundos) throws NotNegative {
        if (segundos <= 0)
            throw new NotNegative("Segundos não podem ser menor ou igual a zero");
        movimento = Movimento.RETARDADO;
        t = INTERVALO_DE_TEMPO;
        double max = segundos;
        double val = 0;
        while (segundos > 0 && this.velocidadeAtual > 0) {
            segundos = atualize(segundos);
            val += t;
            show(val, max);
        }
    }

    @Override
    public void acaoMaterVelocidade(double segundos) throws NotNegative {
        if (segundos <= 0)
            throw new NotNegative("Segundos não podem ser menor ou igual a zero");
        movimento = Movimento.CONSTANTE;
        t = INTERVALO_DE_TEMPO;
        double max = segundos;
        double val = 0;
        this.velocidadeAtual = Util.toKMpH(v);
        while (segundos > 0) {
            segundos = atualize(segundos);
            val += t;
            show(val, max);
        }
    }

    public void parar() throws NotNegative {
        double segundos = Util.getTempo(-a, v, 0);
        this.acaoFrear(segundos);
        System.out.print("\nParou!");
        sleep(3, SLEEP, "!");
        this.resumo();
    }

    private double atualize(double segundos) throws NotNegative {
        if (this.getVolumeAtualDeCombustivel() <= 0)
            throw new SemCombustivel(this);
        if (segundos < INTERVALO_DE_TEMPO)
            t = segundos;
        segundos -= INTERVALO_DE_TEMPO;
        double a = this.a;
        v0 = v;
        _s0 = _s;
        if (movimento == Movimento.CONSTANTE)
            _s = Util.getDeslocamentoMRU(v, _s0, t);
        else {
            if (movimento == Movimento.RETARDADO)
                a = a * -1;
            v = Util.getVelocidade(a, v0, 0, t);
            if (movimento == Movimento.ACELERADO) {
                double veloMaxima = Util.toMpS(this.velocidadeMaxima);
                if (v > this.topSpeed) {
                    if (v > veloMaxima) {
                        v = veloMaxima;
                        this.topSpeed = v;
                        if (segundos > 0)// manter velocidade pelo tempo restante!
                            this.acaoMaterVelocidade(segundos);
                        else
                            segundos = 0;
                    }
                    this.topSpeed = v;
                }
            }
            if (movimento == Movimento.RETARDADO)
                if (v < 0)
                    v = 0;
            this.velocidadeAtual = Util.toKMpH(v);
            _s = Util.getDeslocamentoMRUV(v0, _s0, a, t);
        }
        _Δs = _s.subtract(_s0);
        setDeslocamentoMetros(_Δs);
        this.incrementaTempo(t);
        this.setConsumoMedioInstante(_Δs.doubleValue());
        return segundos;
    }

    private void setConsumoMedioInstante(double s) throws SemCombustivel {
        BigDecimal _cM = BigDecimal.valueOf(this.consumoMedio);
        BigDecimal _ΔS = BigDecimal.valueOf(s);
        _ΔS = _ΔS.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL32).setScale(6, RoundingMode.HALF_EVEN);
        this.consumoMedioInstante = _ΔS.divide(_cM, MathContext.DECIMAL32).setScale(6, RoundingMode.HALF_EVEN)
                .doubleValue();
        if (this.volumeDeCombustivel - this.consumoMedioInstante > 0) {
            this.volumeDeCombustivel -= this.consumoMedioInstante;
        } else {
            this.volumeDeCombustivel = 0;
            throw new SemCombustivel(this);
        }
    }

    public void resumo() {
        // App.limparTela();
        System.out.printf("\n%22s: %s", "Carro", this.nomeModedo);
        System.out.printf("\n%22s: %.5f Km/h", "Velocidade Máxima", Util.toKMpH(this.topSpeed));
        System.out.printf("\n%22s: %.5f Km/h", "Velocidade Média",
                Util.toKMpH(Util.getVelocidadeMedia(0, _s.doubleValue(), 0, this.tempo)));
        System.out.printf("\n%22s: %.5f Km/h", "Velocidade Atual", this.velocidadeAtual);
        System.out.printf("\n%22s: %.3f KM", "Deslocamento Total", this.quilometragem);
        System.out.printf("\n%22s: %.4fs", "Tempo do Deslocamento", this.tempo);
        System.out.printf("\n%22s: %.4f L", "Nível de Combustível", this.volumeDeCombustivel);
        System.out.printf("\n%22s: %.4f L de Combustível", "Consumo", getConsumoMedioTotal());
        System.out.printf("\n%23s %.2f por quilômetro rodado", "R$", this.precoLitro / this.consumoMedio);
    }

    private void show(double val, double max) {
        App.limparTela();
        System.out.printf("\n%22s: %s", "Carro", this.nomeModedo);
        if (movimento == Movimento.RETARDADO)
            System.out.printf("\n%22s: %.3f m/s²", "Desaceleração", -a);
        else
            System.out.printf("\n%22s: %.3f m/s²", "Aceleração", a);
        System.out.printf("\n%22s: %.4f Km/h", "Velocidade Atual", this.velocidadeAtual);
        System.out.printf("\n%22s: %.4f Km\n", "Deslocamento", this.quilometragem);
        System.out.printf("\n%22s: %.5f L", "Volume de Combustível", this.volumeDeCombustivel);
        System.out.printf("%22s: %.2f de %.2f\n", "Tempo", val, max);
        System.out.printf("\n%22s: %.4f L de Combustível", "Consumo", getConsumoMedioTotal());
        if (SLEEP_SHOW > 1)
            sleep(1, SLEEP_SHOW, ".");
    }

    private void setDeslocamentoMetros(BigDecimal _ds) {
        this.quilometragem = this.quilometragem.add(_ds.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL32));
    }

    @Override
    public double getVelocidadeAtual() {
        return this.velocidadeAtual;
    }

    public void acaoPilotoAutomaticoKM(double kmph, double km) throws NotNegative, InterruptedException {
        double segundos = Util.getTempo(km, kmph);
        segundos += Util.getTempo(a, v, Util.toMpS(kmph));
        this.acaoPilotoAutomatico(kmph, segundos);
    }

    public void acaoPilotoAutomatico(double kmph, double segundos) throws NotNegative, InterruptedException {
        if (segundos <= 0) {
            throw new NotNegative("Segundos não podem ser menor ou igual a zero");
        }
        double veloEscolhida = Util.toMpS(kmph);
        double tempoNecessario = 0;
        if (v != veloEscolhida) {
            System.out.print("\nPiloto automático: Equalizando a velocidade!");
            sleep(4, SLEEP, "!");
            if (v > veloEscolhida) {
                tempoNecessario = Util.getTempo(-a, v, veloEscolhida);
                if (segundos < tempoNecessario) {
                    tempoNecessario = segundos;
                }
                this.acaoFrear(tempoNecessario);
            } else {
                tempoNecessario = Util.getTempo(a, v, veloEscolhida);
                if (segundos < tempoNecessario) {
                    tempoNecessario = segundos;
                }
                this.acaoAcelerar(tempoNecessario);
            }
            segundos -= tempoNecessario;
            System.out.printf("\nPiloto automático: ( %.6fs ) Tempos gasto na equalização!", tempoNecessario);
            sleep(3, SLEEP, "!");
        }
        if (segundos > 0) {
            this.acaoMaterVelocidade(segundos);
            System.out.printf("\nPiloto automático: Velocidade mantida por %.2fs ", segundos);
            sleep(3, SLEEP, "!");
        } else {
            System.out.print("\nPiloto automático só conseguiu chegar a essa velocidade com o tempo selecionado!");
            sleep(3, SLEEP, "!");
        }
        System.out.print("\nPiloto automático desligado!");
        sleep(3, SLEEP, "!");
    }

    /**
     * 
     * @param repeticao repetição
     * @param tempo     tempo
     */
    private void sleep(int repeticao, double tempo, String caracter) {
        try {
            while (repeticao-- > 0) {
                Thread.sleep((long) (tempo));
                System.out.print(caracter);
            }
        } catch (InterruptedException e) {
        }
        System.out.println();
    }

    @Override
    public double getVolumeAtualDeCombustivel() {
        return this.volumeDeCombustivel;
    }

    public double getConsumoMedio() {
        return consumoMedio;
    }

    public double getConsumoMedioTotal() {
        return this.volumeDeCombustivelInicial - this.volumeDeCombustivel;
    }

    public double getAceleracaoMedia() {
        return aceleracaoMedia;
    }

    public double getVelocidadeMaxima() {
        return velocidadeMaxima;
    }

    public double getQuilometragem() {
        return quilometragem.divide(BigDecimal.valueOf(1000), MathContext.DECIMAL64).doubleValue();
    }

    public double getQuilometragemX() {
        return quilometragem.doubleValue();
    }

    public String getNomeModedo() {
        return nomeModedo;
    }

    private void incrementaTempo(double tempo) {
        this.tempo += tempo;
    }
}