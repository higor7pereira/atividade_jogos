
package br.mackenzie.jogos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bando {

    Agente[] agentes = new Agente[30];
    Vector2[] forcas = new Vector2[30];

    public Bando() {
        float largura = Gdx.graphics.getWidth();
        float altura = Gdx.graphics.getHeight();

        for (int i = 0; i < agentes.length; i++) {
            float x = largura / 2f + MathUtils.random(-110f, 110f);
            float y = altura / 2f + MathUtils.random(-110f, 110f);

            agentes[i] = new Agente(x, y);
            agentes[i].maxVelocidade = 150f;
            agentes[i].maxForca = 250f;

            agentes[i].velocidade.set(
                MathUtils.random(-1f, 1f),
                MathUtils.random(-1f, 1f)
            );

            if (agentes[i].velocidade.isZero()) {
                agentes[i].velocidade.set(1, 0);
            }

            agentes[i].velocidade.nor().scl(90f);
            forcas[i] = new Vector2();
        }
    }

    public void atualizar(float delta) {

        for (int i = 0; i < agentes.length; i++) {
            Agente atual = agentes[i];

            Vector2 separacao = new Vector2();
            Vector2 alinhamento = new Vector2();
            Vector2 coesao = new Vector2();

            int vizinhos = 0;
            int proximos = 0;

            for (int j = 0; j < agentes.length; j++) {
                if (i == j) continue;

                Agente outro = agentes[j];
                float distancia = atual.posicao.dst(outro.posicao);

                if (distancia > 0 && distancia < 90f) {
                    alinhamento.add(outro.velocidade);
                    coesao.add(outro.posicao);
                    vizinhos++;
                }

                if (distancia > 0 && distancia < 35f) {
                    Vector2 fuga = new Vector2(atual.posicao)
                        .sub(outro.posicao)
                        .nor()
                        .scl(1f / distancia);

                    separacao.add(fuga);
                    proximos++;
                }
            }

            if (proximos > 0) {
                separacao.scl(1f / proximos);
                separacao.nor().scl(atual.maxVelocidade);
                separacao.sub(atual.velocidade);
                separacao.limit(atual.maxForca);
            }

            if (vizinhos > 0) {
                alinhamento.scl(1f / vizinhos);
                alinhamento.nor().scl(atual.maxVelocidade);
                alinhamento.sub(atual.velocidade);
                alinhamento.limit(atual.maxForca);

                coesao.scl(1f / vizinhos);
                coesao.sub(atual.posicao);
                coesao.nor().scl(atual.maxVelocidade);
                coesao.sub(atual.velocidade);
                coesao.limit(atual.maxForca);
            }

            forcas[i].setZero();
            forcas[i].mulAdd(separacao, 1.7f);
            forcas[i].mulAdd(alinhamento, 1.0f);
            forcas[i].mulAdd(coesao, 0.85f);
            forcas[i].limit(320f);
        }

        for (int i = 0; i < agentes.length; i++) {
            Agente a = agentes[i];

            a.velocidade.mulAdd(forcas[i], delta);
            a.velocidade.limit(a.maxVelocidade);
            a.atualizar(delta);

            float largura = Gdx.graphics.getWidth();
            float altura = Gdx.graphics.getHeight();

            if (a.posicao.x < 0) a.posicao.x += largura;
            if (a.posicao.x >= largura) a.posicao.x -= largura;
            if (a.posicao.y < 0) a.posicao.y += altura;
            if (a.posicao.y >= altura) a.posicao.y -= altura;
        }
    }

    public void desenhar(ShapeRenderer shape) {
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 1f, 0.8f, 1f);

        for (Agente a : agentes) {
            shape.circle(a.posicao.x, a.posicao.y, 6);
        }

        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(1f, 1f, 1f, 1f);

        for (Agente a : agentes) {
            Vector2 direcao = new Vector2(a.velocidade);

            if (!direcao.isZero()) {
                direcao.nor().scl(14f);

                shape.line(
                    a.posicao.x,
                    a.posicao.y,
                    a.posicao.x + direcao.x,
                    a.posicao.y + direcao.y
                );
            }
        }

        shape.end();
    }
}
