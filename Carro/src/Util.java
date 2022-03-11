import java.math.BigDecimal;
import java.math.MathContext;

public abstract class Util {
    private static final int _60 = 60;

    /**
     * @param horas = Horas [ Ex: 1.30 = 90]
     * @return ( Minutos )
     */
    public static long converte_Horas_em_Minutos(double horas) {
        BigDecimal hrs = bigDecOf(horas);
        BigDecimal minutos = hrs.add(new BigDecimal(hrs.toBigInteger().negate()));
        minutos = minutos.multiply(new BigDecimal(100));
        int min = minutos.intValue();
        if (min > 59)
            min = 59;
        min += bigDecOf(horas).intValue() * _60;
        return min;
    }

    /**
     * @param kmPorHora ( Km/h )
     * @return M/s
     */
    public static double toMpS(double kmPorHora) {
        return new BigDecimal(kmPorHora).divide(BigDecimal.valueOf(3.6), MathContext.DECIMAL32)
                .doubleValue();
    }

    /**
     * @param metrosPorSegundos ( M/s )
     * @return Km/h
     */
    public static double toKMpH(double metrosPorSegundos) {
        return new BigDecimal(metrosPorSegundos).multiply(BigDecimal.valueOf(3.6), MathContext.DECIMAL32)
                .doubleValue();
    }

    /**
     * @param _ΔsKm = Distância ( Km's )
     * @param kmph  = Velocidade ( Km/h )
     * @return t ( em Segundos )
     * @apiNote Formula da Velocidade Média para calcular o tempo gasto para
     *          percorrer a
     *          distância.
     */
    public static double getTempo(double _ΔsKm, double kmph) {
        BigDecimal metros = BigDecimal.valueOf(_ΔsKm).multiply(BigDecimal.valueOf(1000), MathContext.DECIMAL32);
        BigDecimal mps = BigDecimal.valueOf(toMpS(kmph));
        return metros.divide(mps).doubleValue();
    }

    /**
     * @param a  = Aceleração (m/s²)
     * @param v0 = Velocidade Inicial ( m/s )
     * @param v  = Velocidade Final ( m/s )
     * @return segundos
     * @apiNote
     *          <b>v = v0 + a.t ( em m/s² )
     *          <p>
     *          Legenda:</b>
     *          <p>
     *          <code>
     *          a – aceleração<p>
     *          Δv – variação de velocidade<p>
     *          Δt – intervalo de tempo<p>
     *          v – velocidade final<p>
     *          v0 – velocidade inicial<p>
     *          t – instante final<p>
     *          </code>
     *          <b>Use essa função quando:</b>
     *          <p>
     *          <li>Você souber o valor da velocidade inicial (<b><i>v0</i></b>), da
     *          aceleração (<b><i>a</i></b>),
     *          da velocidade final (<b><i>v</i></b>) e quiser calcular o tempo
     *          (<b><i>t</i></b>).
     */
    public static double getTempo(double a, double v0, double v) {
        BigDecimal _a = bigDecOf(a);
        BigDecimal _Δv = bigDecOf(v).subtract(bigDecOf(v0));
        return _Δv.divide(_a, MathContext.DECIMAL32).doubleValue();
    }

    /**
     * @param v0 = Velocidade Inicial ( m/s )
     * @param v  = Velocidade Final ( m/s )
     * @param t0 = Tempo Inicial ( seg )
     * @param t  = Tempo Final ( seg )
     * @return m/s²
     * @apiNote
     *          <b>a = (v - v0) / (t - t0)
     *          <p>
     *          Legenda:</b>
     *          <p>
     *          <code>
     *          a – aceleração<p>
     *          Δv – variação de velocidade<p>
     *          Δt – intervalo de tempo<p>
     *          v – velocidade final<p>
     *          v0 – velocidade inicial<p>
     *          t – instante final<p>
     *          t0 – instante inicial<p>
     *          </code>
     *          <b>Use essa função quando:</b>
     *          <p>
     *          <li>Você souber o valor da velocidade inicial (<b><i>v0</i></b>), da
     *          velocidade final
     *          (<b><i>v</i></b>), do tempo (<b><i>t</i></b>), mas quiser calcular a
     *          aceleração (<b><i>a</i></b>);
     */
    public static double getAceleracao(double v0, double v, double t0, double t) {
        BigDecimal _Δv = bigDecOf(v).subtract(bigDecOf(v0));
        BigDecimal _Δt = bigDecOf(t).subtract(bigDecOf(t0));
        return _Δv.divide(_Δt, MathContext.DECIMAL32).doubleValue();
    }

    /**
     * @param a  = Aceleração (m/s²)
     * @param v0 = Velocidade Inicial ( m/s )
     * @param t0 = Tempo Inicial ( seg )
     * @param t  = Tempo Final ( seg )
     * @return m/s
     * @apiNote
     *          <b>v = v0 + a.t
     *          <p>
     *          Legenda:</b>
     *          <p>
     *          <code>
     *          a – aceleração<p>
     *          v0 – velocidade inicial<p>
     *          t – instante final<p>
     *          t0 – instante inicial<p>
     *          Δt – intervalo de tempo<p>
     *          </code>
     *          <b>Use essa função quando:</b>
     *          <p>
     *          <li>Você souber o valor da velocidade inicial (<b><i>v0</i></b>), da
     *          aceleração (<b><i>a</i></b>) e do tempo (<b><i>t</i></b>) e quiser
     *          calcular a velocidade final
     *          (<b><i>v</i></b>).
     */
    public static double getVelocidade(double a, double v0, double t0, double t) {
        BigDecimal _a = bigDecOf(a);
        BigDecimal _v0 = bigDecOf(v0);
        BigDecimal _Δt = bigDecOf(t).subtract(bigDecOf(t0));
        return _v0.add(_Δt.multiply(_a)).doubleValue();
    }

    public static double getVelocidadeMedia(double s0, double s, double t0, double t) {
        BigDecimal _Δs = bigDecOf(s).subtract(bigDecOf(s0));
        BigDecimal _Δt = bigDecOf(t).subtract(bigDecOf(t0));
        return _Δs.divide(_Δt, MathContext.DECIMAL32).doubleValue();
    }

    public static double getDeslocamentoTorricelli(double v0, double v, double a) {
        BigDecimal _v0 = bigDecOf(v0);
        BigDecimal _v = bigDecOf(v);
        BigDecimal _a = bigDecOf(a);
        return _v.pow(2).subtract(_v0.pow(2)).divide(bigDecOf(2).multiply(_a), MathContext.DECIMAL32).doubleValue();
    }

    /**
     * @apiNote <b> S =s0 + v0.t + (a.t²)/2
     * @param v0  <b> Velocidade Inicial
     * @param _s0 <b> Ponto Inicial
     * @param a   <b> Aceleração
     * @param t   <b> Tempo
     * @return
     */
    public static BigDecimal getDeslocamentoMRUV(double v0, BigDecimal _s0, double a, double t) {
        BigDecimal _a = bigDecOf(a);
        BigDecimal _t = bigDecOf(t);
        BigDecimal _v0 = bigDecOf(v0).multiply(_t); // v0.t
        _t = _t.pow(2);// t²
        _a = _a.multiply(_t);// a.t²
        _a = _a.divide(bigDecOf(2));// a.t²/2
        return _s0.add(_v0.add(_a));// soma
    }

    /**
     * @apiNote <b> S = S0 + v.t
     * @param v   <b> Velocidade
     * @param _s0 <b> Ponto Inicial
     * @param t   <b> Tempo
     * @return
     */
    public static BigDecimal getDeslocamentoMRU(double v, BigDecimal _s0, double t) {
        BigDecimal _vt = bigDecOf(v).multiply(bigDecOf(t)); // v.t
        return _s0.add(_vt);// soma
    }

    private static BigDecimal bigDecOf(double dec) {
        return BigDecimal.valueOf(dec);
    }
}
