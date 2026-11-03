
package br.mackenzie.jogos;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Main extends ApplicationAdapter {

    ShapeRenderer shape;
    SpriteBatch batch;
    BitmapFont font;

    Agente agente;
    Bando bando;

    Vector2 alvo;
    Vector2 jogador;
    Vector2 velocidadeJogador;

    int modo = 1;
    boolean temAlvo = false;
    boolean perseguindo = false;

    @Override
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        agente = new Agente(320, 240);
        bando = new Bando();

        alvo = new Vector2(320, 240);
        jogador = new Vector2(550, 240);
        velocidadeJogador = new Vector2();
    }

    @Override
    public void render() {
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 0.05f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            modo = 1;
            temAlvo = false;
            agente.velocidade.setZero();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            modo = 2;
            temAlvo = false;
            agente.velocidade.setZero();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            modo = 3;
            perseguindo = false;
            agente.velocidade.setZero();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            modo = 4;
        }

        if (modo == 4) {
            bando.atualizar(delta);

            Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            bando.desenhar(shape);

            batch.begin();
            font.setColor(1, 1, 1, 1);

            font.draw(batch,
                "MODO 4 - BANDO: 30 AGENTES",
                12, Gdx.graphics.getHeight() - 12);

            font.draw(batch,
                "Separacao | Alinhamento | Coesao",
                12, Gdx.graphics.getHeight() - 35);

            font.draw(batch,
                "Pressione 1, 2 ou 3 para voltar",
                12, Gdx.graphics.getHeight() - 58);

            batch.end();
            return;
        }

        if (Gdx.input.justTouched() && modo != 3) {
            alvo.set(
                Gdx.input.getX(),
                Gdx.graphics.getHeight() - Gdx.input.getY()
            );
            temAlvo = true;
        }

        if (modo == 1 && temAlvo) {
            agente.seek(alvo, delta);
        }

        if (modo == 2 && temAlvo) {
            agente.arrive(alvo, delta);
        }

        if (modo == 3) {
            atualizarJogador(delta);

            float distancia = agente.posicao.dst(jogador);

            if (distancia < 170f) {
                perseguindo = true;
            } else if (distancia > 240f) {
                perseguindo = false;
            }

            if (perseguindo) {
                agente.pursuit(jogador, velocidadeJogador, delta);
            } else {
                agente.wander(delta);
            }
        }

        agente.atualizar(delta);

        if (modo == 3) {
            agente.limitarTela(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            );
        }

        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shape.begin(ShapeRenderer.ShapeType.Line);

        if (modo != 3 && temAlvo) {
            shape.setColor(1, 1, 0, 1);
            shape.circle(alvo.x, alvo.y, 12);
        }

        if (modo == 3) {
            shape.setColor(0.4f, 0.4f, 0.5f, 1);
            shape.circle(
                agente.posicao.x,
                agente.posicao.y,
                170
            );
        }

        shape.setColor(1, 1, 1, 1);
        shape.line(
            agente.posicao.x,
            agente.posicao.y,
            agente.posicao.x + agente.velocidade.x * 0.25f,
            agente.posicao.y + agente.velocidade.y * 0.25f
        );

        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(0, 1, 0.8f, 1);
        shape.circle(
            agente.posicao.x,
            agente.posicao.y,
            15
        );

        if (modo == 3) {
            shape.setColor(1, 0.3f, 0.3f, 1);
            shape.circle(jogador.x, jogador.y, 15);
        }

        shape.end();

        batch.begin();

        font.setColor(1, 1, 1, 1);
        font.draw(batch,
            "1 - SEEK | 2 - ARRIVE | 3 - WANDER + FSM | 4 - BANDO",
            12, Gdx.graphics.getHeight() - 12);

        if (modo == 1) {
            font.draw(batch,
                "SEEK - Clique para definir o destino",
                12, Gdx.graphics.getHeight() - 35);
        }

        if (modo == 2) {
            font.draw(batch,
                "ARRIVE - Clique para definir o destino",
                12, Gdx.graphics.getHeight() - 35);
        }

        if (modo == 3) {
            String estado = perseguindo ? "PURSUIT" : "WANDER";

            font.draw(batch,
                "FSM: " + estado + " | Mova o jogador com WASD",
                12, Gdx.graphics.getHeight() - 35);
        }

        batch.end();
    }

    private void atualizarJogador(float delta) {
        velocidadeJogador.setZero();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocidadeJogador.y += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocidadeJogador.y -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocidadeJogador.x -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocidadeJogador.x += 1;
        }

        if (!velocidadeJogador.isZero()) {
            velocidadeJogador.nor().scl(180f);
        }

        jogador.mulAdd(velocidadeJogador, delta);

        jogador.x = Math.max(
            20,
            Math.min(
                Gdx.graphics.getWidth() - 20,
                jogador.x
            )
        );

        jogador.y = Math.max(
            20,
            Math.min(
                Gdx.graphics.getHeight() - 20,
                jogador.y
            )
        );
    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();
    }
}
