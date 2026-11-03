
package br.mackenzie.jogos;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Agente {

    Vector2 posicao;
    Vector2 velocidade;

    float maxVelocidade = 200f;
    float maxForca = 350f;
    float anguloVagar = 0f;

    public Agente(float x, float y) {
        posicao = new Vector2(x, y);
        velocidade = new Vector2();
    }

    public void atualizar(float delta) {
        posicao.mulAdd(velocidade, delta);
    }

    private void aplicarForca(Vector2 desejada, float delta) {
        Vector2 forca = desejada.sub(velocidade);
        forca.limit(maxForca);

        velocidade.mulAdd(forca, delta);
        velocidade.limit(maxVelocidade);
    }

    public void seek(Vector2 alvo, float delta) {
        Vector2 desejada = new Vector2(alvo).sub(posicao);

        if (desejada.len2() < 1f) return;

        desejada.nor().scl(maxVelocidade);
        aplicarForca(desejada, delta);
    }

    public void arrive(Vector2 alvo, float delta) {
        Vector2 desejada = new Vector2(alvo).sub(posicao);
        float distancia = desejada.len();

        if (distancia < 7f && velocidade.len() < 20f) {
            velocidade.setZero();
            return;
        }

        float velocidadeDesejada = maxVelocidade;

        if (distancia < 130f) {
            velocidadeDesejada *= distancia / 130f;
        }

        if (distancia > 0f) {
            desejada.scl(velocidadeDesejada / distancia);
        }

        aplicarForca(desejada, delta);
    }

    public void wander(float delta) {
        anguloVagar += MathUtils.random(-1.5f, 1.5f) * delta;

        Vector2 desejada = new Vector2(
            MathUtils.cos(anguloVagar),
            MathUtils.sin(anguloVagar)
        ).scl(maxVelocidade * 0.65f);

        aplicarForca(desejada, delta);
    }

    public void pursuit(Vector2 jogador,
                        Vector2 velocidadeJogador,
                        float delta) {

        float distancia = posicao.dst(jogador);
        float previsao = Math.min(distancia / maxVelocidade, 0.7f);

        Vector2 futuro = new Vector2(jogador)
            .mulAdd(velocidadeJogador, previsao);

        seek(futuro, delta);
    }

    public void limitarTela(float largura, float altura) {
        if (posicao.x < 20) {
            posicao.x = 20;
            velocidade.x = Math.abs(velocidade.x);
            anguloVagar = 0;
        }

        if (posicao.x > largura - 20) {
            posicao.x = largura - 20;
            velocidade.x = -Math.abs(velocidade.x);
            anguloVagar = MathUtils.PI;
        }

        if (posicao.y < 20) {
            posicao.y = 20;
            velocidade.y = Math.abs(velocidade.y);
            anguloVagar = MathUtils.PI / 2;
        }

        if (posicao.y > altura - 20) {
            posicao.y = altura - 20;
            velocidade.y = -Math.abs(velocidade.y);
            anguloVagar = -MathUtils.PI / 2;
        }
    }
}
