package com.renatoandrade.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import javax.xml.soap.Text;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle birdCirculo;
	private Rectangle canoTopoR;
	private Rectangle canoBaixoR;
//	private ShapeRenderer shapeR;
//	private int movimento =0;


	//Atributos de Configuracao

	private float larguraDisp;
	private float alturaDisp;

	private Random numRandom;
	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float startVert;
	private float startHor;
	private float posicaoMovCanoHor;
	private float espacoCano;
	private float deltaT;
	private float alturaEntreCano;
	private int intervalo = 400;
	private int gameState = 0 ;// 0 -> JOGO NAO INICIADO / 1-> JOGO INICIADO 2-> TELA DE GAME OVER
	private int velocidadeCano = 270;
	private int pontuacao = 0;
	private int posBirdHor = 120;
	private boolean marcouPonto = false;

	//CAMERA
	private OrthographicCamera camera;
	private Viewport viewport;
	private  final float VIRTUAL_WIDTH = 768;
	private  final float VIRTUAL_HEIGHT = 1024;



	@Override
	public void create () {

		this.batch = new SpriteBatch();
		this.numRandom = new Random();
		this.birdCirculo = new Circle();
//		canoTopoR = new Rectangle();
//		canoBaixoR = new Rectangle();

		this.passaros = new Texture[3];
		this.passaros[0] =  new Texture("passaro1.png");
		this.passaros[1] =  new Texture("passaro2.png");
		this.passaros[2] =  new Texture("passaro3.png");
		this.fundo  = new Texture("fundo.png");
		this.canoBaixo = new Texture("cano_baixo.png");
		this.canoTopo =  new Texture("cano_topo.png");
		this.gameOver  = new Texture("game_over.png");

		this.fonte = new BitmapFont();
		this.fonte.setColor(Color.WHITE);
		this.fonte.getData().setScale(6);
		this.mensagem = new BitmapFont();
		this.mensagem.setColor(Color.WHITE);
		this.mensagem.getData().setScale(3);

		//CONFIGURACAO DA CAMERA

		this.camera = new OrthographicCamera();
		this.camera.position.set(this.VIRTUAL_WIDTH/2,this.VIRTUAL_HEIGHT/2,0);
		this.viewport = new StretchViewport(this.VIRTUAL_WIDTH,this.VIRTUAL_HEIGHT,camera);

		this.larguraDisp = this.VIRTUAL_WIDTH;
		this.alturaDisp = this.VIRTUAL_HEIGHT;
		this.startVert = this.alturaDisp / 2;
		this.posicaoMovCanoHor = this.larguraDisp;
		this.espacoCano = 300.0F;
		this.velocidadeCano = 270;

//		shapeR = new ShapeRenderer();
	}

	@Override
	public void render () {
		this.camera.update();

		//Limpar frames
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//movimento do passaro
		this.deltaT = Gdx.graphics.getDeltaTime();
		this.variacao += this.deltaT * 10.0;
		if (this.variacao > 2.0F)
			this.variacao = 0.0F;

		//VERIFICA SE O JOGO FOI INICIADO
		if (this.gameState == 0) {
			if (Gdx.input.justTouched()) {
				this.gameState = 1;
			}
			//JOGO INICIADO
		} else {
			this.velocidadeQueda++;
			if (this.startVert > 0.0 || this.velocidadeQueda < 0.0) {
				this.startVert -= this.velocidadeQueda;
			}

			if(this.gameState == 1){
				this.posicaoMovCanoHor -= this.deltaT * this.velocidadeCano;
				if (Gdx.input.justTouched()) {
					this.velocidadeQueda = -15.0F;
				}

				//Verifica se os canos sairam da tela
				if (this.posicaoMovCanoHor < (float)-(this.canoTopo.getWidth())) {

					//aumenta a dificuldade do jogo gradativamente
					if(this.pontuacao%3 == 0 && this.pontuacao > 0 && this.pontuacao < 10){
						this.espacoCano -= 30;
						this.velocidadeCano += 25;
					}
					if(this.pontuacao == 10){
						this.velocidadeCano += 30;
					}

					this.posicaoMovCanoHor = this.larguraDisp;
					this.alturaEntreCano = this.numRandom.nextInt(this.intervalo) - this.intervalo / 2;
					this.marcouPonto = false;
				}
				if(this.posicaoMovCanoHor < this.posBirdHor && this.marcouPonto == false){
					this.pontuacao++;
					this.marcouPonto = true;
				}

				//TELA DE GAMEOVER
			}else  if(Gdx.input.justTouched()){
				this.gameState = 0;
				this.pontuacao = 0;
				this.velocidadeQueda = 0;
				this.startVert = this.alturaDisp / 2.0F;
				this.posicaoMovCanoHor = this.larguraDisp;
				this.espacoCano = 300;
				this.velocidadeCano = 270;
				this.marcouPonto = false;
			}



			//Gdx.app.log("Variacao", "Variacao:  "+Gdx.graphics.getDeltaTime());

		}
		this.batch.setProjectionMatrix(camera.combined);

		this.batch.begin();
		this.batch.draw( fundo, 0, 0, larguraDisp, alturaDisp );
		this.batch.draw( canoBaixo, posicaoMovCanoHor, alturaDisp / 2 - canoBaixo.getHeight() - espacoCano / 2 + alturaEntreCano) ;
		this.batch.draw( canoTopo, posicaoMovCanoHor, alturaDisp / 2 + espacoCano / 2 + alturaEntreCano );
		this.batch.draw( passaros[(int) variacao], posBirdHor, startVert );
		this.fonte.draw(batch,String.valueOf(pontuacao), larguraDisp/2 , alturaDisp - 50);

		if(this.gameState == 2){
			//MENSAGEM DE GAME OVER
			this.mensagem.draw(this.batch, "Toque para jogar novamente",  this.larguraDisp /2 - 275, this.alturaDisp /2 - this.gameOver.getHeight()/2);
			this.batch.draw(this.gameOver, this.larguraDisp/2 - this.gameOver.getWidth()/2, this.alturaDisp/2);
		}

		this.batch.end();
		//FORMAS DOS OBJETOS
		this.birdCirculo.set(this.posBirdHor+ this.passaros[0].getWidth()/2, this.startVert + this.passaros[0].getHeight()/2,
				this.passaros[0].getWidth()/2);
		this.canoBaixoR = new Rectangle(
				this.posicaoMovCanoHor, this.alturaDisp / 2 - this.canoBaixo.getHeight() - this.espacoCano / 2 + this.alturaEntreCano,
				this.canoBaixo.getWidth(), this.canoBaixo.getHeight());
		this.canoTopoR = new Rectangle(
				this.posicaoMovCanoHor, this.alturaDisp / 2 + this.espacoCano / 2 + this.alturaEntreCano,
				this.canoTopo.getWidth(),this.canoTopo.getHeight());



		//DESENHAR AS FORMAS
/*		shapeR.begin(ShapeRenderer.ShapeType.Filled);
		shapeR.circle( birdCirculo.x,birdCirculo.y,birdCirculo.radius )/;
		shapeR.rect(canoBaixoR.x,canoBaixoR.y,canoBaixoR.width,canoBaixoR.height);
		shapeR.rect(canoTopoR.x,canoTopoR.y,canoTopoR.width,canoTopoR.height);
		shapeR.setColor(Color.RED);
		shapeR.end();
*/
		//TESTE DE COLISAO

		if(Intersector.overlaps(birdCirculo, canoBaixoR) || Intersector.overlaps(birdCirculo,canoTopoR)
				|| startVert <= 0 || startVert >= alturaDisp - passaros[0].getHeight()){
//			Gdx.app.log("Colisao","Houve Colisao");
			gameState  = 2;
		}

	}

	@Override
	public void dispose () {
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
