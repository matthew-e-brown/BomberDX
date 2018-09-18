import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class BomberDX extends PApplet {

//Menu Types
boolean loadScreen, mainGame, optionsMenu, defeatScreen, victoryScreen, credScreen = false;
boolean mainMenu = true;
//End of menu types

//Game Loading
boolean gameLoadStarted, gridLoadStarted, explosionLoadStarted, bombLoadStarted, planeLoadStarted, deadPlaneLoadStarted = false;
boolean gameLoadCompleted, gridLoadCompleted, explosionLoadCompleted, bombLoadCompleted, planeLoadCompleted, deadPlaneLoadCompleted = false;
int amtLoaded  = 0;
int loadStages = 822;
//End of game loading

PFont bold, regular, light;

//Global Sounds
String soundsPath;
//

//Main Menu Variables
PImage logo;
int playButton = 0, opButton = 1, credButton = 2, quitButton = 3;
Button[] mB = new Button[4];
//End of main menu variables

//Options Menu Variables
int distSlider = 0, maxPlane = 1;
floatSlider[] oS = new floatSlider[2];
Switch planeToggle;
int RENDERDIST = 9, maxPlanes = 15; //To limit the size of the array, we'll have to use if (array.size() < limiter) array.add(thing)

int backButton = 0;
Button[] oB = new Button[1];
//End of options menu variables

//mainGame Variables
PlayerPlane player;
ArrayList<EnemyPlane> enemies;
int basesKilled, totalBases, totalSpawned, maxEnemy;
Tile[][] grid;
float grounddepth = 0;
PImage bg, pausebg, pauselogo, optionsbg, optionslogo, creditsbg, creditslogo;
PImage crosshair, loadingBar, loadingBar2, checkmark, quit0, bombIcon, baseIcon, healthIcon;

boolean paused = false;
Button[] pB = new Button[4];
int resume = 0, home = 1, restart = 2, quit = 3; 

ArrayList<Shot> playerProjectiles; //list of the player's bullets
ArrayList<Shot> enemyProjectiles; //list of enemy bullets
ArrayList<Explosion> explosions; //list of all explosions
//End of mainGame variables

PImage[] explosionStages, bombStages;
PImage[][] planeStages;
PImage[][] deadPlaneStages;
int b17 = 0, b29 = 1, a6mzero = 2, bf109 = 3, i16 = 4, p51 = 5, spitfire = 6;
int curPlane = 0;

int timeSinceDead, timeSinceWon;
PImage defeatBG, victoryBG, defeatTitle, victoryTitle;
Button[] eB = new Button[3];
int eBrestart = 0, eBmenu = 1, eBquit = 2;

public void setup() {
   //P3D is used so we can get shadows and the ground is farther away.
  frameRate(60);

  bold = createFont("Assets/Fonts/Europa-Bold.otf", 30);
  regular = createFont("Assets/Fonts/Europa-Regular.otf", 30);
  light = createFont("Assets/Fonts/Europa-Light.otf", 30);

  bg = loadImage("Assets/Sprites/bg.png");
  pausebg = loadImage("Assets/Sprites/bgpaused.png");
  optionsbg = loadImage("Assets/Sprites/bgoptions.png");
  creditsbg = loadImage("Assets/Sprites/bgcredits.png");

  logo = loadImage("Assets/Sprites/logo.png");
  logo.resize(525, 175);
  pauselogo = loadImage("Assets/Sprites/pausedlogo.png");
  optionslogo = loadImage("Assets/Sprites/optionslogo.png");
  creditslogo = loadImage("Assets/Sprites/creditslogo.png");

  loadingBar = loadImage("Assets/Sprites/loadingbar3.png");
  loadingBar2 = loadImage("Assets/Sprites/loadingbar2.png");

  checkmark = loadImage("Assets/Sprites/checkmark.png");
  checkmark.resize(20, 20);

  quit0 = loadImage("Assets/Sprites/buttons/quit0.png");

  bombIcon = loadImage("Assets/Sprites/hud/bomb.png");
  baseIcon = loadImage("Assets/Sprites/hud/base.png");
  healthIcon = loadImage("Assets/Sprites/hud/health.png");
  healthIcon.resize(29, 29);

  {//Global Sounds
    soundsPath = sketchPath("/Assets/Sounds/Jeckkech_meeshfeesh-sounds") + "/";
  }//

  mainMenuSetup();
}

public void draw() {
  if (mainMenu) {
    background(bg);
    imageMode(CENTER);
    image(logo, width/2, height/4);
    for (Button button : mB) {
      button.update();
      button.display();
    }
  } else if (optionsMenu) {
    background(optionsbg);
    image(optionslogo, 560, 235);
    for (Button button : oB) {
      button.update();
      button.display();
    }

    for (floatSlider slider : oS) {
      textAlign(CENTER, CENTER);
      fill(0);
      textFont(bold);
      textSize(30);
      text(slider.title, slider.x, slider.y - slider.h*1.5f);
      slider.update();
      slider.display();
    }

    fill(0);
    textAlign(CENTER, CENTER);    
    textFont(bold);
    textSize(30);
    text(planeToggle.title, planeToggle.x, planeToggle.y - planeToggle.h*1.5f);
    planeToggle.display();

    if (oS[distSlider].mouseOver() || oS[maxPlane].mouseOver()) cursor(HAND);
    else cursor(ARROW);

    RENDERDIST = round(oS[distSlider].getVar());
    maxPlanes = round(oS[maxPlane].getVar());
    curPlane = planeToggle.getVarAsInt();
  } else if (credScreen) {
    background(creditsbg);
    image(creditslogo, 560, 235);
    for (Button button : oB) {
      button.update();
      button.display();
    }
    
    fill(0);
    textAlign(CENTER, CENTER);
    textFont(bold);
    textSize(34);
    text("MATTHEW BROWN", width/2-35*8, height/2-35*3);
    text("NICK BROWN", width/2+35*8, height/2-35*3);
    text("THEO NGUYEN", width/2-35*8, height/2+35);
    text("HASGRAPHICS.COM", width/2+35*8, height/2+35);

    textFont(regular);
    textSize(18);
    text("@MattShnoop", width/2-35*8, height/2-35*2.25f);
    text("@drunkbear95", width/2+35*8, height/2-35*2.25f);
    text("@neosndt", width/2-35*8, height/2+35*1.75f);

    textFont(light);
    textSize(28);
    text("Lead Programmer, Project Head", width/2-35*8, height/2-35*1.5f);
    text("Programmer", width/2+35*8, height/2-35*1.5f);
    text("Design Coordinator, Graphics", width/2-35*8, height/2+35*2.5f);
    text("Supplementary Explosion Graphic", width/2+35*8, height/2+35*2.5f);
  } else if (loadScreen) {
    background(bg);
    pushMatrix();
    {
      float h = 50;
      float w = 800;
      translate(width/2, height/2);
      rectMode(CENTER);
      imageMode(CENTER);
      fill(50);
      noStroke();
      //rect(0, 0, w, h, h/2);
      image(loadingBar2, 0, 0);
      fill(0xffFF0D0D);
      rectMode(CORNER);
      fill(0);
      rect(+w/2-5, -h/2+5, map(amtLoaded, loadStages, 0, 0, -w+11), h-11, 25);

      fill(0);
      textAlign(CENTER, CENTER);
      textFont(bold);
      textSize(30);
      text("Loading... " + round(map(amtLoaded, loadStages, 0, 100, 0)) + "%", 0, -60);

      textAlign(LEFT, CENTER);
      textFont(bold);
      textSize(24);

      float exoffset = -80;
      float whyspacing = 40;
      float checkspacer = 45;
      imageMode(CENTER);

      if (!gameLoadStarted) fill(200);
      else if (gameLoadStarted) fill(0);
      text("Loading Main Game", exoffset, 80+whyspacing*0);
      if (gameLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*0);

      if (!planeLoadStarted) fill(200);
      else if (planeLoadStarted) fill(0);
      text("Loading Plane Sprites & Animations", exoffset, 80+whyspacing*1);
      if (planeLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*1);

      if (!deadPlaneLoadStarted) fill(200);
      else if (deadPlaneLoadStarted) fill(0);
      text("Loading Damaged Plane Sprites & Animations", exoffset, 80+whyspacing*2);
      if (deadPlaneLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*2);

      if (!bombLoadStarted) fill(200);
      else if (bombLoadStarted) fill(0);
      text("Loading Bomb Sprites", exoffset, 80+whyspacing*3);
      if (bombLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*3);

      if (!explosionLoadStarted) fill(200);
      else if (explosionLoadStarted) fill(0);
      text("Loading Explosion Animation", exoffset, 80+whyspacing*4);
      if (explosionLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*4);

      if (!gridLoadStarted) fill(200);
      else if (gridLoadStarted) fill(0);
      text("Initializing World Map", exoffset, 80+whyspacing*5);
      if (gridLoadCompleted) image(checkmark, exoffset - checkspacer, 80 + whyspacing*5);
    }
    popMatrix();
    grounddepth = -750;
    if (!gameLoadStarted /*temporary, until plane spawning implemented:*/ && (deadPlaneLoadCompleted && planeLoadCompleted)) thread("mainGameSetup");
    if (!planeLoadStarted) thread("loadPlaneImages");
    if (!deadPlaneLoadStarted) thread("loadDeadPlaneImages");
    if (!bombLoadStarted) thread("loadBombImages");
    if (!explosionLoadStarted) thread("loadExplosionImages");
    if (!gridLoadStarted && grounddepth != 0) thread("createGrid");

    if (amtLoaded == loadStages && (gameLoadCompleted && planeLoadCompleted && deadPlaneLoadCompleted && bombLoadCompleted && explosionLoadCompleted && gridLoadCompleted)) {
      loadScreen = false;
      mainGame = true;
    }
  } else if (mainGame) { // ------------------------------------------- START OF IF(MAINGAME)
    background(0, 135, 255);
    pushMatrix();
    { // --------------------- START OF MAIN GAME MATRIX (PUSH)
      if (!paused) {
        translate(width/2, height/2);
        cursor(crosshair);
        // Updating:
        updateGrid(grid, player);

        // Check collisions with enemy planes & player shots
        for (EnemyPlane plane : enemies) {
          for (Shot proj : playerProjectiles) {
            if (dist(proj.pos.x, proj.pos.y, plane.pos.x, plane.pos.y) <= 48 /*&& abs(proj.pos.z - plane.pos.z) < 2*/ ) {
              plane.health--;
              proj.health -= 2;
            }
          }
        }

        // Check collisions with enemy projectiles & player's plane
        for (Shot proj : enemyProjectiles) {
          if (dist(proj.pos.x, proj.pos.y, player.pos.x, player.pos.y) <= player.sprite.width*0.75f) {
            player.health--;
          }
        }

        //Add a new plane
        if (enemies.size() < maxPlanes && totalSpawned < maxEnemy && floor(random(0, 180)) < 5) {
          totalSpawned++;
          int randpick = floor(random(0, 5));
          int pick = 0;
          if (randpick == 0) pick = a6mzero;
          else if (randpick == 1) pick = bf109;
          else if (randpick == 2) pick = spitfire;
          else if (randpick == 3) pick = i16;
          else if (randpick == 4) pick = p51;
          enemies.add(new EnemyPlane(random(-1280, 1280), random(-1280, 1280), pick));
        }

        //Check the health of each enemy plane
        for (EnemyPlane plane : enemies) {
          plane.update(player.pos);
          if (plane.health <= 0 && plane.dead == false) { 
            plane.kill();
            explosions.add(new Explosion(plane.pos.x, plane.pos.y, plane.pos.z, 0.5f));
          }
        }

        //Check the health of the player
        if (player.health <= 0 && !player.dead) {
          player.kill();
          explosions.add(new Explosion(player.pos.x, player.pos.y, player.pos.z));
        }

        //Check the health of the player's shots
        for (Shot proj : playerProjectiles) {
          if (proj.health <= 0 || proj.timeAlive >= frameRate*3) proj.kill();
        }

        //Check the health of the enemy's shots
        for (Shot proj : enemyProjectiles) {
          if (proj.health <= 0 || proj.timeAlive >= frameRate*2) proj.kill();
        }

        //Remove enemy planes from their list if they're dead -- needs to be done with non-enhanced for loop to avoid null error
        for (int i = 0; i < enemies.size(); i++) {
          EnemyPlane plane = enemies.get(i);
          if (plane.dead == true && plane.pos.z < grounddepth) {
            explosions.add(new Explosion(plane.pos.x, plane.pos.y, grounddepth+1, 1.2f));
            enemies.remove(i);
          }
        }

        //Remove player projectiles from their list
        for (int i = 0; i < playerProjectiles.size(); i++) {
          Shot s = playerProjectiles.get(i);
          if (s.dead) playerProjectiles.remove(i);
        }

        //Remove enemy projectiles from their list
        for (int i = 0; i < enemyProjectiles.size(); i++) {
          Shot s = enemyProjectiles.get(i);
          if (s.dead) enemyProjectiles.remove(i);
        }

        if (dist(player.pos.x, player.pos.y, 0, 0) > 2500) player.health--;

        if (player.pos.z > grounddepth) player.update();
        else if (player.pos.z < grounddepth && !player.grounded) {
          player.grounded = true;
          explosions.add(new Explosion(player.pos.x, player.pos.y, grounddepth+1));
        }

        if (player.grounded) timeSinceDead++;
        if (basesKilled >= totalBases) timeSinceWon++;

        if (timeSinceDead >= 4*frameRate && !victoryScreen && !defeatScreen) {
          defeatScreen = true;
          mainGame = false;
        } else if (timeSinceWon >= 5*frameRate && !victoryScreen && !defeatScreen) {
          victoryScreen = true;
          mainGame = false;
        }
        //End of Updating

        //Display:
        if (player.gridPosX - RENDERDIST < 0) {
          player.gridPosX = RENDERDIST;
        }
        if (player.gridPosY - RENDERDIST < 0) {
          player.gridPosY = RENDERDIST;
        }
        if (player.gridPosX + RENDERDIST >= grid.length) {
          player.gridPosX = grid.length - 1 - RENDERDIST;
        }
        if (player.gridPosY + RENDERDIST >= grid.length) {
          player.gridPosY = grid.length - 1 - RENDERDIST;
        }        
        for (int i = player.gridPosX - RENDERDIST; i < player.gridPosX + RENDERDIST + 1; i++) {
          for (int j = player.gridPosY - RENDERDIST; j < player.gridPosY + RENDERDIST + 1; j++) {
            grid[i][j].display();
          }
        }

        for (int i = 0; i < grid.length; i++) {
          for (Tile t : grid[i]) {
            if (t.base == true && t.health > 0) {
              if (dist(0, 0, t.displaypos.x, t.displaypos.y) <= width/3) t.healthBar();
            }
          }
        }

        if (player.pos.z > grounddepth) player.displayShadow(grounddepth+1);
        for (EnemyPlane plane : enemies) plane.displayShadow(grounddepth+1); 
        for (Explosion explosion : explosions) if (explosion.pos.z > grounddepth + 1.5f) explosion.displayShadow(player.pos, grounddepth+1);

        for (int i = 0; i < explosions.size(); i++) {
          Explosion exp = explosions.get(i);
          exp.update();
          exp.display(player.pos);
          if (exp.dead) explosions.remove(i);
        }

        if (player.pos.z > grounddepth) player.display();
        for (EnemyPlane plane : enemies) plane.display();

        for (Shot proj : playerProjectiles) {
          proj.updateShot(player.pos);
          proj.displayShot();
          proj.displayShadow(grounddepth+1);
        } // -- End of projectiles for loop

        for (Shot proj : enemyProjectiles) {
          proj.updateShot(player.pos);
          proj.displayShot();
          proj.displayShadow(grounddepth+1);
        }
        //End of Display
      } else if (paused) { //end of if !paused
        cursor(ARROW);
        background(pausebg);
        image(pauselogo, 560, 235);
        for (Button button : pB) {
          button.update();
          button.display();
        }
        if (!pB[quit].mouseOver()) image(quit0, 70, 70);
      }

      //End of displaying
    } // --------------------- END OF MAIN GAME MATRIX (POP)
    popMatrix();
    if (!paused && !defeatScreen && !victoryScreen) {
      player.healthBar(15, 15, 575, 30);
      player.drawBombCounter(15, 15+45);
      remainingBases(15, 15+45+45);
    }

    textAlign(RIGHT, TOP);
    fill(0);
    fill(0, 255, 0);
    textFont(bold);
    textSize(14);
    text("FPS: " + floor(frameRate), width-30, 30, 10);
    //text("Spawned: " + totalSpawned, width-50, 60, 10);
  } else if (victoryScreen || defeatScreen) { // ----------------------- END OF IF(MAINGAME)
    //filter(GRAY);
    imageMode(CORNER);
    cursor(ARROW);
    if (victoryScreen) {
      //tint(255, map(85, 0, 100, 0, 255));
      image(victoryBG, 0, 0);
      tint(255, 255);
      image(victoryTitle, 0, 0);
    } else if (defeatScreen) {
      //tint(255, map(85, 0, 100, 0, 255));
      image(defeatBG, 0, 0);
      tint(255, 255);
      image(defeatTitle, 0, 0);
    }
    for (Button button : eB) {
      button.update();
      button.display();
    }
    if (!eB[eBquit].mouseOver()) image(quit0, 70, 70);
  }
}

public void updateGrid(Tile[][] g, PlayerPlane player) {
  for (int i = 0; i < g.length; i++) {
    for (int j = 0; j < g[0].length; j++) {
      g[i][j].displaypos.x = -player.pos.x-(128*g.length/2)+128*i;
      g[i][j].displaypos.y = -player.pos.y-(128*g[0].length/2)+128*j;
    }
  }
}

public void remainingBases(float x, float y) {
  pushMatrix(); 
  {
    translate(x, y, 1);
    rectMode(CORNER);
    image(baseIcon, 0, 0);
    for (int i = 0; i < totalBases; i++) {
      stroke(0);
      strokeWeight(2);
      if (i < totalBases - basesKilled) fill(lerpColor(0xff5512DE, 0xff5FC4EE, map(i, 0, totalBases, 0, 1)));
      else fill(50);
      rect(baseIcon.width + 16 + 12 * i, 0, 10, 25, 3);
    }
  }  
  popMatrix();
}

public void mainMenuSetup() {// // --- Main menu setup
  //Button(float x, float y, float w, float h, String text, float size) {
  mB[playButton] = new Button(560, 367.5f, 560, 105, "Play Game", 30, "playgame");
  mB[opButton] = new Button(560, 507.5f, 560, 105, "Options", 30, "options");
  mB[credButton] = new Button(437.5f, 647.5f, 315, 105, "Credits", 30, "credits");
  mB[quitButton] = new Button(735, 647.5f, 210, 105, "Quit", 30, "quit");
}// // --- end of main menu setup

public void optionsMenuSetup() {// // --- Options menu setup
  oS[distSlider] = new floatSlider(787.5f, 405, 305, 30, "Render Distance", "render");
  oS[maxPlane] = new floatSlider(787.5f, 510, 305, 30, "Max Enemy Planes", "max");

  oS[distSlider].setVar(RENDERDIST, 0, 9);
  oS[maxPlane].setVar(maxPlanes, 4, 20);

  oB[backButton] = new Button(560, 647.5f, 210, 105, "Back", 30, "back");
  planeToggle = new Switch(332.5f, 457.5f, 160, 30, "Pick a Plane", 18);
  planeToggle.setVar(curPlane, "B17", "B29");
}// // --- end of options menu setup

public void mainGameSetup() {// // --- Main game setup
  gameLoadStarted = true;
  println("Main game Started");
  crosshair = loadImage("Assets/Sprites/crosshair.png");
  crosshair.resize(40, 40);
  amtLoaded++;

  player = new PlayerPlane(0, 0, curPlane);
  enemies = new ArrayList<EnemyPlane>();
  //enemies = new EnemyPlane[8];   
  playerProjectiles = new ArrayList<Shot>(); //list of the player's bullets
  enemyProjectiles = new ArrayList<Shot>(); //list of enemy bullets
  explosions = new ArrayList<Explosion>(); //list of all explosions
  basesKilled = 0;
  totalBases = 0;
  totalSpawned = 0;
  maxEnemy = 0;
  timeSinceDead = 0;
  timeSinceWon = 0;
  amtLoaded++;

  //Button(float x, float y, float w, float h, String text, float size) {
  pB[resume] = new Button(560, 367.5f, 350, 105, "Resume", 30, "resume");
  pB[resume].centre = true;
  pB[resume].textInvert = true;
  pB[home] = new Button(560, 647.5f, 350, 105, "Main Menu", 30, "mainmenu");
  pB[home].centre = true;
  pB[home].textInvert = true;
  pB[restart] = new Button(560, 507.5f, 350, 105, "Restart", 30, "restart");
  pB[restart].centre = true;
  pB[restart].textInvert = true;
  pB[quit] = new Button(70, 70, 70, 70, "", 1, "quit1");

  //Button[] eB = new Button[3];
  //int eBrestart = 0, eBmenu = 1, eBquit = 2;
  eB[eBrestart] = new Button(560, 507.5f, 350, 105, "Restart", 30, "restart");
  eB[eBrestart].centre = true;
  eB[eBrestart].textInvert = true;
  eB[eBmenu] = new Button(560, 647.5f, 350, 105, "Main Menu", 30, "mainmenu");
  eB[eBmenu].centre = true;
  eB[eBmenu].textInvert = true;
  eB[eBquit] = new Button(70, 70, 70, 70, "", 1, "quit1");

  victoryBG = loadImage("Assets/Sprites/victorybg.png");
  defeatBG = loadImage("Assets/Sprites/defeatbg.png");
  victoryTitle = loadImage("Assets/Sprites/victorytext.png");
  defeatTitle = loadImage("Assets/Sprites/defeattext.png");

  amtLoaded++;

  println("Main game completed");
  gameLoadCompleted = true;
  amtLoaded += 1;
}// // --- end of main game setup

public void createGrid() {
  gridLoadStarted = true;
  println("Grid create Started");
  grid = new Tile[25][25];

  //Create grid
  for (int i = 0; i < grid.length; i++) {
    for (int j = 0; j < grid[0].length; j++) {
      int randint = floor(random(0, 11));
      grid[i][j] = new Tile(0-(128*grid.length/2)+128*i, 0-(128*grid[0].length/2)+128*j, grounddepth, randint);
      amtLoaded++;
    }
  }

  //Put 25 bases in random places
  int b = 0;
  while (b < 25) { //while loop so that it doesn't put 2 bases in one place
    int i = floor(random(0, 25));
    int j = floor(random(0, 25));
    if (!grid[i][j].base) {
      grid[i][j] = new Tile(0-(128*grid.length/2)+128*i, 0-(128*grid[0].length/2)+128*j, grounddepth, floor(random(11, 15)));
      totalBases++;
      amtLoaded++;
      b++;
    }
  }

  println("Total bases: " + totalBases);
  println("Grid created");
  gridLoadCompleted = true;
  amtLoaded++;
}

public void loadExplosionImages() {
  explosionLoadStarted = true;
  println("Explosion images started");
  explosionStages = new PImage[74];
  for (int i = 0; i < explosionStages.length; i++) {
    explosionStages[i] = loadImage("Assets/Sprites/explosion/" + i + ".png");
    amtLoaded++;
  }
  println("Explosion images loaded");
  explosionLoadCompleted = true;
  amtLoaded++;
}

//PImage[] explosionStages, bombStages;
//PImage[] a6mzeroStages, b17Stages, b29Stages, bf109Stages, i16Stages, p51Stages, spitfireStages;
//int b17 = 0, b29 = 1, a6mzero = 2, bf109 = 3, i16 = 4, p51 = 5, spitfire = 6;

public void loadBombImages() {
  bombLoadStarted = true;
  println("Bomb sprites started");
  bombStages = new PImage[5];
  for (int i = 0; i < bombStages.length; i++) {
    bombStages[i] = loadImage("Assets/Sprites/bomb/" + i + ".png");
    amtLoaded++;
  }
  println("Bomb sprites completed");
  bombLoadCompleted = true;
  amtLoaded++;
}

public void loadPlaneImages() {
  planeLoadStarted = true;
  println("Plane sprites started");
  planeStages = new PImage[7][10];
  for (int i = 0; i < planeStages[b17].length; i++) {
    planeStages[b17][i] = loadImage("Assets/Sprites/b17/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[b29].length; i++) {
    planeStages[b29][i] = loadImage("Assets/Sprites/b29/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[a6mzero].length; i++) {
    planeStages[a6mzero][i] = loadImage("Assets/Sprites/a6mzero/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[bf109].length; i++) {
    planeStages[bf109][i] = loadImage("Assets/Sprites/bf109/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[i16].length; i++) {
    planeStages[i16][i] = loadImage("Assets/Sprites/i16/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[p51].length; i++) {
    planeStages[p51][i] = loadImage("Assets/Sprites/p51/" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < planeStages[spitfire].length; i++) {
    planeStages[spitfire][i] = loadImage("Assets/Sprites/spitfire/" + i + ".png");
    amtLoaded++;
  }
  println("Plane sprites completed");
  planeLoadCompleted = true;
  amtLoaded++;
}

public void loadDeadPlaneImages() {
  deadPlaneLoadStarted = true;
  println("Plane_dead sprites started");
  deadPlaneStages = new PImage[7][2];
  for (int i = 0; i < deadPlaneStages[b17].length; i++) {
    deadPlaneStages[b17][i] = loadImage("Assets/Sprites/b17/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[b29].length; i++) {
    deadPlaneStages[b29][i] = loadImage("Assets/Sprites/b29/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[a6mzero].length; i++) {
    deadPlaneStages[a6mzero][i] = loadImage("Assets/Sprites/a6mzero/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[bf109].length; i++) {
    deadPlaneStages[bf109][i] = loadImage("Assets/Sprites/bf109/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[i16].length; i++) {
    deadPlaneStages[i16][i] = loadImage("Assets/Sprites/i16/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[p51].length; i++) {
    deadPlaneStages[p51][i] = loadImage("Assets/Sprites/p51/dead" + i + ".png");
    amtLoaded++;
  }
  for (int i = 0; i < deadPlaneStages[spitfire].length; i++) {
    deadPlaneStages[spitfire][i] = loadImage("Assets/Sprites/spitfire/dead" + i + ".png");
    amtLoaded++;
  }
  println("Plane_dead sprites completed");
  deadPlaneLoadCompleted = true;
  amtLoaded++;
}
class Bomb {
  PVector pos, displaypos, vel, acc;
  PImage sprite;
  PImage[] stages = new PImage[5];
  int spriteStage;
  float SCALE = 1;
  
  int gridPosX = 13, gridPosY = 13;
  
  Bomb(PVector initialPos, PVector initialVel/*, int spriteFolder*/) {
    this.pos = initialPos.copy();
    this.displaypos = new PVector(0, 0, 0);
    this.vel = initialVel.copy();
    vel.x *= 0.65f;
    vel.y *= 0.65f;
    acc = new PVector(-vel.x*0.002f, -vel.y*0.002f, -0.2f);
    for (int i = 0; i < stages.length; i++) {
      stages[i] = bombStages[i];
    }
    sprite = stages[0];
  }

  public void update(PVector dropperpos) {
    vel.add(acc);
    pos.add(vel);
    
    gridPosX = floor(pos.x/128) + floor(grid.length/2) + 1;
    gridPosY = floor(pos.y/128) + floor(grid.length/2) + 1;
    
    displaypos.x = -dropperpos.x + pos.x;
    displaypos.y = -dropperpos.y + pos.y;
    
    spriteStage++;
    if (spriteStage == stages.length) spriteStage = 0;
    if (frameCount % 3 == 0) sprite = stages[spriteStage];    
  }
  
  public void drawsprite(float x, float y) {
    image(sprite, x, y);
  }

  public void display() {
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y, pos.z);
      rotate(PI/2+vel.heading());
      scale(SCALE);
      drawsprite(0, 0);
    }
    popMatrix();
  }
  
  public void displayShadow(float depth) {
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y, depth);
      tint(0, 0, 0, 128);
      rotate(PI/2+vel.heading());
      scale(SCALE);
      drawsprite(0, 0);
      tint(255, 255);
    }
    popMatrix();
  }
  
}
class Explosion {
  PVector pos;
  PImage sprite;
  PImage[] spriteStages;
  int s;
  boolean dead;
  float scale = 1;
  Explosion(float x, float y, float z) {
    pos = new PVector(x, y, z);

    spriteStages = new PImage[explosionStages.length];

    for (int i = 0; i < explosionStages.length; i++) {
      spriteStages[i] = explosionStages[i];
    }

    sprite = spriteStages[0];
    s = 0;
  }

  Explosion(float x, float y, float z, float scale) {
    pos = new PVector(x, y, z);
    spriteStages = new PImage[explosionStages.length];
    for (int i = 0; i < explosionStages.length; i++) {
      spriteStages[i] = explosionStages[i];
    }
    sprite = spriteStages[0];
    s = 0;
    this.scale = scale;
  }

  public void update() {
    s++;
    if (s >= spriteStages.length) dead = true;
    if (!dead) sprite = spriteStages[s];
  }

  public void display(PVector player) {
    pushMatrix();
    {
      translate(-player.x + pos.x, -player.y + pos.y, pos.z);
      imageMode(CENTER);
      scale(scale);
      image(sprite, 0, 0);
    }
    popMatrix();
  }

  public void displayShadow(PVector player, float depth) {
    pushMatrix(); 
    {
      translate(-player.x + pos.x +map(pos.z, 120, depth, -depth*0.2f, 0), -player.y + pos.y + map(pos.z, 120, depth, -depth*0.2f, 0), depth);
      scale(scale);

      imageMode(CENTER);
      tint(0, 0, 0, 128);
      image(sprite, 0, 0);
      tint(255, 255);
    }
    popMatrix();
  }
}
class Button {
  float x, y, w, h, tSize;
  PImage sprite;
  String text;
  int fill, stroke, hoverFill, hoverStroke, nFill, nStroke;
  boolean image;
  boolean centre, textInvert = false;
  Button(float x, float y, float w, float h, String text, float size) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.text = text;
    this.tSize = size;
    hoverFill = color(160);
    hoverStroke = color(80, 185, 255);
    nFill = color(245);
    nStroke = color(75);
    fill = nFill;
    stroke = nStroke;
    image = false;
  }


  Button(float x, float y, float w, float h, String text, float size, String sprite) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.text = text;
    this.tSize = size;
    fill = color(245);
    stroke = color(75);
    this.sprite = loadImage("Assets/Sprites/buttons/" + sprite + ".png");
    image = true;
  }

  public boolean mouseOver() {
    boolean ex = false;
    boolean why = false;
    if (mouseX >= x - w/2 && mouseX <= x + w/2) ex = true;
    if (mouseY >= y - h/2 && mouseY <= y + h/2) why = true;
    if (ex && why) return true;
    else return false;
  }

  public void update() {
    if (!image) {
      if (!mouseOver()) {
        fill = nFill;
        stroke = nStroke;
      } else if (mouseOver()) {
        fill = hoverFill;
        stroke = hoverStroke;
      }
    }
  }

  public void display() {
    pushMatrix();
    {
      translate(x, y);
      rectMode(CENTER);
      fill(fill);
      if (!mouseOver()) {
        noStroke();
        //stroke(stroke);
        //strokeWeight(4);
        rect(0, 0, w, h, 25);
      }
      imageMode(CENTER);
      if (image && mouseOver()) image(sprite, 0, 0);
      else if (!image) rect(0, 0, w, h, 25);
      fill(0);
      if (textInvert && mouseOver()) fill(255);
      if (centre) textAlign(CENTER, CENTER);
      else textAlign(LEFT, CENTER);
      textFont(bold);
      textSize(tSize);
      if (!centre) text(text, -w/2 + 35, 0);
      else text(text, 0, 0);
    }
    popMatrix();
  }
}

class floatSlider {
  float x, y, w, h;
  float min, max;
  float var;
  float sliderpos;
  boolean held = false;
  String title;
  PImage gradient;
  boolean grad = false;
  floatSlider(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }

  floatSlider(float x, float y, float w, float h, String title) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.title = title;
  }

  floatSlider(float x, float y, float w, float h, String title, String gradient) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.title = title;
    this.gradient = loadImage("Assets/Sprites/sliders/" + gradient + ".png");
    this.gradient.resize(round(this.w), this.gradient.height);
    grad = true;
  }

  public void setVar(float var, float min, float max) {
    this.var = var;
    this.min = min;
    this.max = max;
    this.sliderpos = map(var, min, max, x-w/2, x+w/2);
  }

  public float getVar() {
    return var;
  }

  public boolean mouseOver() {
    if (dist(mouseX, mouseY, sliderpos, y) <= h) return true;
    else return false;
  }

  public void update() {
    if (held) {
      sliderpos = constrain(mouseX, x-w/2, x+w/2);
      var = map(sliderpos, x-w/2, x+w/2, min, max);
    }
  }

  public void display() {
    pushMatrix();
    {
      rectMode(CENTER);
      translate(x, y);
      fill(255);
      stroke(0);
      strokeWeight(2);
      if (!grad) rect(0, 0, w, h/2, h/4);
      else {
        image(gradient, 0, 0);
        rectMode(CORNERS);
        fill(0);
        noStroke();
        rect(sliderpos-x, -gradient.height/2, w/2, gradient.height/2, h/4);
      }
      pushMatrix();
      {
        translate(sliderpos-x, 0, 2);
        fill(255);
        stroke(0);
        ellipse(0, 0, h, h);
      }
      popMatrix();
    }
    popMatrix();

    if (mouseOver() || held) {
      pushMatrix(); 
      {
        rectMode(CENTER);
        translate(sliderpos, y + h*1.85f, 1);
        fill(255);
        stroke(0);
        int roundvar = round(var);
        if (roundvar - 100 < 0) rect(0, 0, h*1.5f, h*1.5f);
        else if (roundvar - 100 >= 0) rect(0, 0, h*1.85f, h*1.5f);
        fill(0);
        textAlign(CENTER, CENTER);
        textFont(bold);
        textSize(24);
        text(roundvar, 0, 0);
      } 
      popMatrix();
    }
  }
}

class Switch { //inb4 DMCA from Nintendo
  float x, y, w, h, r;
  boolean var;
  int textSize;
  String[] LRtext = new String[2];
  String title;
  Switch(float x, float y, float w, float h, String title, int textSize) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.r = h*7/8;
    this.title = title;
    this.textSize = textSize;
  }

  public void setVar(boolean var) { 
    this.var = var;
  }

  public void setVar(boolean var, String left, String right) {
    this.var = var;
    this.LRtext[0] = left;
    this.LRtext[1] = right;
  }

  public void setVar(int var) {
    if (var == 0) this.var = false;
    else if (var == 1) this.var = true;
    else println("ERROR! Slider expects int of 0 or 1");
  }

  public void setVar(int var, String left, String right) {
    if (var == 0) this.var = false;
    else if (var == 1) this.var = true;
    else println("ERROR! Slider expects int of 0 or 1");
    this.LRtext[0] = left;
    this.LRtext[1] = right;
  }

  public boolean getVar() {
    return var;
  }

  public int getVarAsInt() {
    if (!var) return 0;
    else return 1;
  }

  public boolean mouseOver() {
    boolean wid = false;
    boolean hei = false;
    if (mouseX >= x - w/2 && mouseX <= x + w/2) wid = true;
    if (mouseY >= y - h/2 && mouseY <= y + h/2) hei = true;
    if (wid && hei) return true;
    else return false;
  }

  public void display() {
    pushMatrix(); 
    {
      translate(x, y);
      rectMode(CENTER);
      strokeWeight(2);
      stroke(0);
      fill(255);
      rect(0, 0, w, h, r);
      fill(210);
      rectMode(CORNERS);
      float left = 0 - w/2 + w*0.055f;
      float right = 0 + w/2 - w*0.055f;
      float top = 0 - h/2 + w*0.055f;
      float bottom = 0 + h/2 - w*0.055f;
      if (var == false) rect(left, top, 0, bottom, r);
      else if (var == true) rect(0, bottom, right, top, r);
      for (int i = 0; i < LRtext.length; i++) {
        if (LRtext[i] != "") { //False = left, True = right //left = 0; right = 1;
          float boxWidth = LRtext[i].length() * 20;
          rectMode(CENTER);
          fill(255);
          stroke(0);
          strokeWeight(2);
          rect(0 + w/2 + boxWidth*0.65f - i*2*(w/2+boxWidth*0.65f), 0, boxWidth, h*1.12f);
          fill(0);
          textAlign(CENTER, CENTER);
          textFont(bold);
          textSize(textSize);
          text(LRtext[1-i], 0 + w/2 + boxWidth*0.65f - i*2*(w/2+boxWidth*0.65f), 0);
        }
      }
    } 
    popMatrix();
  }
}
boolean up, down, left, right, spacebar = false;

public void keyPressed() {
  if (key == 'w' || key == 'W') up = true;
  if (key == 's' || key == 'S') down = true;
  if (key == 'a' || key == 'A') left = true;
  if (key == 'd' || key == 'D') right = true;
  if (keyCode == 32) spacebar = true;
  if (mainGame && (keyCode == 10 || keyCode == 13)) paused = !paused;
}

public void keyReleased() {
  if (key == 'w' || key == 'W') up = false;
  if (key == 's' || key == 'S') down = false;
  if (key == 'a' || key == 'A') left = false;
  if (key == 'd' || key == 'D') right = false;
  if (keyCode == 32) spacebar = false;
}

public void mousePressed() {
  if (mainMenu) {
    if (mB[playButton].mouseOver()) {
      mainMenu = false;
      loadScreen = true;
      optionsMenu = false;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = false;
      mainGame = false;
    } else if (mB[opButton].mouseOver()) {
      optionsMenuSetup();
      mainMenu = false;
      optionsMenu = true;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = false;
      mainGame = false;
    } else if (mB[credButton].mouseOver()) {
      optionsMenuSetup();
      mainMenu = false;
      optionsMenu = false;
      defeatScreen = false;
      victoryScreen = false;
      credScreen = true;
      mainGame = false;
    } else if (mB[quitButton].mouseOver()) {
      exit();
    }
  } else if (optionsMenu) {
    for (floatSlider s : oS) {
      if (s.mouseOver()) s.held = true;
    }
    if (planeToggle.mouseOver()) planeToggle.var = !planeToggle.var;
    if (oB[backButton].mouseOver()) {
      allButMainMenu();
    }
  } else if (credScreen) {
    if (oB[backButton].mouseOver()) {
      allButMainMenu();
    }
  } else if (victoryScreen || defeatScreen) {
    if (eB[eBrestart].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      backtoLoading();
    } else if (eB[eBmenu].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      allButMainMenu();
    } else if (eB[eBquit].mouseOver()) {
      exit();
    }
  } else if (mainGame && paused) {
    if (pB[resume].mouseOver()) {
      paused = false;
    } else if (pB[home].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      allButMainMenu();
    } else if (pB[restart].mouseOver()) {
      amtLoaded = 0;
      setLoadBoolsFalse();
      setCompBoolsFalse();
      backtoLoading();
    } else if (pB[quit].mouseOver()) {
      exit();
    }
  }
}


public void mouseReleased() {
  if (mainMenu) {
  } else if (optionsMenu) {
    for (floatSlider s : oS) {
      s.held = false;
    }
  }
}

public void setLoadBoolsFalse() {
  gameLoadStarted = false;
  gridLoadStarted = false;
  explosionLoadStarted = false;
  bombLoadStarted = false;
  planeLoadStarted = false;
  deadPlaneLoadStarted = false;
}

public void setCompBoolsFalse() {
  gameLoadCompleted = false;
  gridLoadCompleted = false; 
  explosionLoadCompleted = false; 
  bombLoadCompleted = false; 
  planeLoadCompleted = false; 
  deadPlaneLoadCompleted = false;
}

public void allButMainMenu() {
  mainGame = false;
  paused = false;
  mainMenu = true;
  loadScreen = false;
  optionsMenu = false;
  defeatScreen = false;
  victoryScreen = false;
  credScreen = false;
}

public void backtoLoading() {
  mainGame = false;
  paused = false;
  mainMenu = false;
  loadScreen = true;
  optionsMenu = false;
  defeatScreen = false;
  victoryScreen = false;
  credScreen = false;
}
class Plane {
  PVector pos, vel;
  int timeAlive;

  int spriteStage, deadspriteStage;
  PImage sprite;
  float SCALE = 1;

  PImage[] stages = new PImage[10];
  PImage[] deadstages = new PImage[2];
  float thrust;

  float turnspeed;
  float normalspeed, maxspeed, lowspeed; //Variables placed here to make it easier to control the settings of the plane's speed

  float health, maxHealth;
  float deadAngle;
  boolean dead;

  Plane(float x, float y, float z, int spriteFolder) {
    this.spriteStage = 0;
    this.deadspriteStage = 0;
    this.thrust = 0.05f;

    pos = new PVector(x, y, z);
    vel = new PVector(0, -thrust);

    dead = false;

    for (int i = 0; i < stages.length; i++) {
      stages[i] = planeStages[spriteFolder][i];
    }
    for (int i = 0; i < deadstages.length; i++) {
      deadstages[i] = deadPlaneStages[spriteFolder][i];
    }
  }

  public void update() {
    if (!dead) {
      timeAlive++;
      pos.add(vel);

      //Updating the sprite every frame
      spriteStage++;
      if (spriteStage == stages.length) {
        spriteStage = 0;
      }
      sprite = stages[spriteStage].copy();
      //End of sprite updating
    } else if (dead) {
      if (frameCount % 6 == 0) {
        deadspriteStage++;
        if (deadspriteStage == deadstages.length) {
          deadspriteStage = 0;
        }
        sprite = deadstages[deadspriteStage].copy();
      }
      vel.z -= 0.1f;
      //If it dies on an even frame, it spins one way, and another if it dies on an odd frame
      if (timeAlive % 2 == 0) deadAngle += 2.25f;
      else deadAngle -= 2.25f;
      deadAngle = deadAngle % 360;
      pos.add(vel);
    }
  }
  
  public void kill() {
    deadAngle = degrees(vel.heading());
    dead = !dead;
  }
  
}
class EnemyPlane extends Plane {
  PVector displaypos, shotVel;
  float followdist, muzzleVelocity, coolDown, shotSpread, fireRate;
  float timeToReengage, timeToForceDisengage, timeSinceDisengage, forceEngageCounter;
  boolean engagePlayer;
  boolean canShoot, facing;
  //constant
  int maxCoolDown;
  
  EnemyPlane(float x, float y, int spriteFolder) {
    super(x, y, random(-2, 2), spriteFolder);
    pos.z = 0;
    displaypos = new PVector(x, y, 0);

    health = 20;
    maxHealth = health;

    turnspeed = 1.25f;
    
    maxCoolDown = 500;
    coolDown = maxCoolDown;
    shotSpread = 10;
    fireRate = 10;
    shotVel = new PVector(0, 0);
    muzzleVelocity = 20;

    normalspeed = 8;
    maxspeed = 10;
    lowspeed = 6;

    SCALE = 0.80f;

    followdist = random(225, 650);
    canShoot = true;
    
    timeSinceDisengage = 0;
    forceEngageCounter = 0;
    timeToReengage = 90;
    timeToForceDisengage = 200;
    engagePlayer = true;
  }

  public void update(PVector targpos) {
    if (!dead) {
      faceEnemy(targpos);
      coolDown -= fireRate;

      PVector acc = vel.copy().setMag(thrust);

      //Set speed based on distance from player
      float dist = dist(pos.x, pos.y, targpos.x, targpos.y);

      if (dist > followdist*1.325f && vel.mag() < maxspeed) { //Far
        canShoot = true;
        if (vel.mag() < maxspeed) {
          vel.add(acc);
        }
      } else if (dist < followdist*0.10f) { //Close
        canShoot = false;
        if (pos.z < 120) pos.z += 5;
        if (vel.mag() > lowspeed) {
          vel.sub(acc);
        }
      } else if (dist < followdist*1.325f && dist > followdist*0.45f) { //In between
        canShoot = true;
        if (pos.z > 0) pos.z -= 5;
        else if (pos.z < 0) pos.z += 5;
        if (vel.mag() > normalspeed) {
          vel.sub(acc);
        } else if (vel.mag() < normalspeed) {
          vel.add(acc);
        }
      }
      
      if(canShoot == true && facing && coolDown <= 0/* && abs(targangle - vel.heading()) < PI/5*/){
        shoot(enemyProjectiles);
      }
      
    }
    super.update();
    displaypos.x = -targpos.x + pos.x;
    displaypos.y = -targpos.y + pos.y;
    displaypos.z = pos.z;
  }
  
  public void shoot(ArrayList list) {
      shotVel = vel.copy();
      shotVel.setMag(muzzleVelocity);
      //shotVel.mult(2);
      shotVel.rotate(radians(random(-shotSpread, shotSpread)));

      list.add(new Shot(pos.x, pos.y, shotVel));
      coolDown = maxCoolDown;
  }

  public void display() {
    if (!dead) {
      pushMatrix(); 
      {
        translate(displaypos.x, displaypos.y, displaypos.z);
        scale(SCALE);
        rotate(PI/2+vel.heading());

        if (timeAlive < 0.85f*frameRate) tint(255, 255, 255, map(timeAlive, 0, 0.85f*frameRate, 0, 255)); 
        imageMode(CENTER);
        image(sprite, 0, 0);
        //text(health, 0, 0);
        tint(255, 255, 255, 255);
      }
      popMatrix();
      healthBar();
    } else if (dead) {
      pushMatrix();
      {
        translate(displaypos.x, displaypos.y, displaypos.z);
        scale(SCALE);
        rotate(PI/2+radians(deadAngle));

        imageMode(CENTER);
        //tint(128, 128, 128, 255);
        image(sprite, 0, 0);
        //tint(255, 255, 255, 255);
      }
      popMatrix();
    }
  }

  public void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(displaypos.x+map(pos.z, 120, depth, -depth*0.2f, 0), displaypos.y+map(pos.z, 120, depth, -depth*0.2f, 0), depth);
      scale(SCALE);
      if (!dead) rotate(PI/2+vel.heading());
      else rotate(PI/2+radians(deadAngle));

      imageMode(CENTER);
      if (timeAlive < 0.85f*frameRate) tint(0, 0, 0, map(timeAlive, 0, 0.85f*frameRate, 0, 128));
      else tint(0, 0, 0, 128);
      image(sprite, 0, 0);
      tint(255, 255);
    }
    popMatrix();
  }

  public int quadrant(float angle) {
    int quad = 0;
    if (angle <= 0 && angle > -PI/2) quad = 1;
    else if (angle <= -PI/2 && angle >= -PI) quad = 2;
    else if (angle >= PI/2 && angle <= PI) quad = 3;
    else if (angle > 0 && angle < PI/2) quad = 4;
    return quad;
  }

  public int oppquad(int quad) {
    int opp = 0;
    if (quad == 1) opp = 3;
    else if (quad == 2) opp = 4;
    else if (quad == 3) opp = 1;
    else if (quad == 4) opp = 2;
    return opp;
  }

  public float oppangle(float angle) {
    float opp = 0;
    if (angle < 0) opp = angle + PI;
    else if (angle > 0) opp = angle - PI;
    else if (angle == 0) opp = PI;
    else if (angle == PI) opp = 0;
    return opp;
  }

  float targangle;
  //returns a value from -PI to PI

  public void faceEnemy(PVector targetpos) {
    PVector targpos = targetpos.copy();
    targpos.sub(pos);
    targangle = targpos.heading();
    float veloangle = vel.heading();

    int targquad = quadrant(targpos.heading());
    int opptarg = oppquad(targquad);

    int veloquad = quadrant(vel.heading());

    //Add to the velocity's angle if:
    //1. Same quadrant, velocity is lower than target
    //2. It's in the quadrant to the negative (CCW) direction
    //3. Velocity is in the opposite quadrant to the target, and the velocity is greater than the negative of the target

    //Subtract from the velocity's angle if:
    //4. Same quadrant, velocity is higher than target
    //5. It's in the quadrant to the positive (CW) direction
    //6. Velocity is in the oppositve quadtrant to the target, and the velocity is lower than the negative of the target

    if (veloquad == targquad && veloangle < targangle) { //1.
      //add
      vel.rotate(radians(+turnspeed));
    } else if ( (veloquad == 1 && targquad == 4) || (veloquad == 2 && targquad == 1) || (veloquad == 3 && targquad == 2) || (veloquad == 4 && targquad == 3) ) { //2.
      //add
      vel.rotate(radians(+turnspeed));
    } else if (veloquad == opptarg && oppangle(targangle) < veloangle) { //3,
      //add
      vel.rotate(radians(+turnspeed));
    } else if (veloquad == targquad && veloangle > targangle) { //4.
      //sub
      vel.rotate(radians(-turnspeed));
    } else if ( (veloquad == 1 && targquad == 2) || (veloquad == 2 && targquad == 3) || (veloquad == 3 && targquad == 4) || (veloquad == 4 && targquad == 1) ) { //5.
      //sub
      vel.rotate(radians(-turnspeed));
    } else if (veloquad == opptarg && oppangle(targangle) > veloangle) { //6.
      //sub
      vel.rotate(radians(-turnspeed));
    }
    
    if (abs(veloangle-targangle) < PI/36) facing = true;
    else facing = false;
    
  }

  public void healthBar() {
    pushMatrix();
    {
      float h = sprite.height/6;
      float w = sprite.width*0.80f;
      translate(displaypos.x, displaypos.y+sprite.height*1.2f, displaypos.z);
      rectMode(CENTER);
      fill(50);
      noStroke();
      rect(0, 0, w, h, h/2);
      fill(0xffFF0D0D);
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
    }
    popMatrix();
  }
}
class PlayerPlane extends Plane {
  Turret mainGun;
  Bomb[] bombs = new Bomb[128];
  int lastBomb, lastRestore; //Frame at which the last bomb was dropped & last frame bomb was reloaded
  int b; //Spot in the bomblist
  int bombCount, maxBombs;
  int gridPosX = 13, gridPosY = 13; //stores which grid tile the plane is over
  boolean grounded;
  float bombStrength;

  PlayerPlane(float x, float y, int spriteFolder) {
    super(x, y, 0, spriteFolder);

    health = 350;
    maxHealth = health;

    bombStrength = 40;

    turnspeed = 1;

    normalspeed = 2.25f;
    maxspeed = 6.5f;
    lowspeed = 1.5f;

    SCALE = 1.2f;

    lastBomb = -100;
    b = 0;
    bombCount = 25;
    maxBombs = bombCount;

    mainGun = new Turret(pos.x, pos.y);
    mainGun.pos = pos;
    mainGun.vel = vel;

    grounded = false;
  }

  public void update() {
    super.update();
    if (!dead) {

      gridPosX = floor(pos.x/128) + floor(grid.length/2) + 1;
      gridPosY = floor(pos.y/128) + floor(grid.length/2) + 1;

      mainGun.update();

      if (spacebar && frameCount - lastBomb > frameRate/2.5f && bombCount > 0) {
        b++;
        lastBomb = frameCount;
        if (b == bombs.length) b = 0;
        bombs[b] = new Bomb(pos, vel);
        bombCount--;
      }

      for (int i = 0; i < bombs.length; i++) {
        if (bombs[i] != null) {
          bombs[i].update(pos);
          if (bombs[i].pos.z < grounddepth) {
            if (bombs[i].gridPosX > -1 && bombs[i].gridPosX < grid.length && bombs[i].gridPosY > -1 && bombs[i].gridPosY < grid[0].length) {
              for (int rX = -1; rX < 2; rX++) {
                for (int rY = -1; rY < 2; rY++) {
                  if (bombs[i].gridPosX +rX > -1 && bombs[i].gridPosX + rX < grid.length && bombs[i].gridPosY + rY > -1 && bombs[i].gridPosY + rY < grid[0].length) {
                    Tile tileOver = grid[bombs[i].gridPosX + rX][bombs[i].gridPosY + rY];
                    if (tileOver.base == true) {
                      if (rX == 0 && rY == 0) tileOver.health -= bombStrength;
                      else tileOver.health -= bombStrength * 0.25f;
                      if (!tileOver.dead && tileOver.health <= 0) {
                        tileOver.sprite = loadImage("Assets/Sprites/Tiles/" + tileOver.type + "ruined.png");
                        tileOver.dead = true;
                        maxEnemy++;
                        basesKilled++;
                      }
                    }
                  }
                }
              }
              explosions.add(new Explosion(bombs[i].pos.x, bombs[i].pos.y, grounddepth+1, 1.45f));
            }
            bombs[i] = null;
          }
        }
      }

      if (frameCount - lastBomb > 2.5f*frameRate && frameCount - lastRestore > 0.25f*frameRate && bombCount < maxBombs) {
        bombCount++;
        lastRestore = frameCount;
      }

      PVector acc = vel.copy().setMag(thrust);

      if (left) {
        vel.rotate(radians(-turnspeed));
      }
      if (right) {
        vel.rotate(radians(turnspeed));
      }
      if (up && vel.mag() < maxspeed) { //If the user is pressing up //also if the user isn't already at the max velocity
        vel.add(acc);
      }
      if (down && vel.mag() > lowspeed) { //If the user presses down but isn't below the slowest speed
        vel.sub(acc);
      }
      if (!up && !down) { //If the user presses nothing, the plane returns to normal speed
        if (vel.mag() > normalspeed) {
          vel.sub(acc);
        } else if (vel.mag() < normalspeed) {
          vel.add(acc);
        }
      }

      if (mousePressed) {
        mainGun.shoot(playerProjectiles);
      }
    }
  }

  public void display() {
    for (Bomb b : bombs) {
      if (b != null) {
        b.displayShadow(grounddepth+1.2f);
      }
    }

    for (Bomb b : bombs) {
      if (b != null) {
        b.display();
      }
    }

    pushMatrix(); 
    {
      translate(0, 0, pos.z);
      scale(SCALE);
      if (!dead) rotate(PI/2+vel.heading());
      else rotate(PI/2+radians(deadAngle));

      imageMode(CENTER);
      image(sprite, 0, 0);
    }
    popMatrix();
    mainGun.display();
  }

  public void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(map(pos.z, 120, depth, -depth*0.2f, 0), map(pos.z, 120, depth, -depth*0.2f, 0), depth);
      scale(SCALE);
      if (!dead) rotate(PI/2+vel.heading());
      else rotate(PI/2+radians(deadAngle));

      imageMode(CENTER);
      tint(0, 0, 0, 128);
      image(sprite, 0, 0);
      tint(255, 255);
    }
    popMatrix();
  }

  public void healthBar(float x, float y, float w, float h) {
    pushMatrix();
    {
      translate(x + w/2, y + h/2);
      rectMode(CENTER);
      fill(0);
      stroke(0);
      strokeWeight(2);
      rect(0, 0, w, h, h/2);
      fill(/*#FFC400*/0xffFF0D0D);
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
      image(healthIcon, -270, 0);
    }
    popMatrix();
  }

  public void drawBombCounter(float x, float y) {
    pushMatrix(); 
    {
      translate(x, y, 1);
      rectMode(CORNER);
      strokeWeight(2);
      stroke(0);
      tint(0, 255);
      imageMode(CORNER);
      image(bombIcon, 0, 0);
      tint(255, 255);
      for (int i = 0; i < maxBombs; i++) {
        if (i < bombCount) fill(lerpColor(/*#760000*/ 0xffECCA62, /*#FF0D0D*/ 0xffF62932, map(i, 0, maxBombs, 0, 1)));
        else fill(50);
        rect(bombIcon.width + 20 + 12*i, 0, 10, 25, 3);
      }
    } 
    popMatrix();
  }
}
class Shot {
  float SCALE = 1;
  int timeAlive;

  float health;
  float speed;
  PVector pos, vel;
  PVector displayPos;
  float hue;
  boolean dead;
  
  Shot(float x, float y, PVector v) {
    pos = new PVector(x, y, 1);
    displayPos = new PVector(x, y, 0);
    vel = v.copy();
    speed = vel.mag();
    this.health = 4;
    timeAlive = 0;
  }

  public void updateShot(PVector targpos) {
    pos.add(vel);
    timeAlive++;

    displayPos.x = -targpos.x + pos.x;
    displayPos.y = -targpos.y + pos.y;
    displayPos.z = pos.z;
  }

  public void displayShot() {
    pushMatrix();
    {
      translate(displayPos.x, displayPos.y, displayPos.z);
      rotateZ(PI/2+vel.heading());
      stroke(0);
      strokeWeight(0.75f);
      fill(0xffFFF7CB);
      box(6, 15, 6);
    }
    popMatrix();
  }
  
  public void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(displayPos.x+map(pos.z, 120, depth, -depth*0.2f, 0), displayPos.y+map(pos.z, 120, depth, -depth*0.2f, 0), depth);
      scale(SCALE);
      rotate(PI/2+vel.heading());

      rectMode(CENTER);
      fill(0, 0, 0, 128);
      rect(0, 0, 5, 5);
    }
    popMatrix();
  }
  
  public void kill() {
    dead = !dead;
  }
  
}
class Tile {
  PVector pos, displaypos;
  PImage sprite;

  float health, maxHealth;
  int spin, type;
  boolean base, dead;

  Tile(float x, float y, float z, int type) {
    this.type = type;
    pos = new PVector(x, y, z);
    displaypos = new PVector(x, y, z);
    sprite = loadImage("Assets/Sprites/Tiles/" + type + ".png");
    //rotate randomly
    spin = floor(random(1, 5));
    base = false;
    if (type >= 11 && type <= 14) {
      health = maxHealth = 100;
      base = true;
      dead = false;
    }
  }

  public void display() {
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y, displaypos.z);
      rotate(PI/2 * spin);
      imageMode(CENTER);
      image(sprite, 0, 0);
    }
    popMatrix();
  }

  public boolean isOver(PVector objpos) {
    boolean wid = false;
    boolean hei = false;
    if (objpos.x > pos.x-sprite.width/2 && objpos.x < pos.x+sprite.width/2) wid = true;
    if (objpos.y > pos.y-sprite.height/2 && objpos.y < pos.y+sprite.height/2) hei = true;
    if (wid && hei) return true;
    else return false;
  }

  public void healthBar() {
    float w = sprite.width*0.80f;
    float h = sprite.height/6;
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y+sprite.height*0.65f, displaypos.z);
      rectMode(CENTER);
      fill(50);
      //stroke(0);
      //strokeWeight(2);
      noStroke();
      rect(0, 0, w, h, h/2);
      fill(0xff16A7DE);
      noStroke();
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
    }
    popMatrix();
  }
}
class Turret {
  PVector pos, vel;
  PVector lookangle, shotVel, target;
  float muzzleVelocity, coolDown = 0, shotSpread = 10, fireRate = 10;

  PImage sprite;
  Turret(float x, float y) {
    pos = new PVector(x, y);
    vel = new PVector(0, 0);
    lookangle = new PVector(0, -5);
    //target = new PVector(0, 0);
    shotVel = new PVector(0, 0);
    muzzleVelocity = 20;
    sprite = loadImage("Assets/Sprites/turret.png");
  }

  public void update() {
    //update the cooldown
    if (coolDown > 0) {
      coolDown -= fireRate;
    }

    //aim at the mouse
    lookangle = new PVector(mouseX, mouseY);
    lookangle.sub(new PVector(width/2, height/2));
  }

  public void shoot(ArrayList list) {
    if (coolDown <= 0) {

      shotVel = lookangle;
      shotVel.setMag(muzzleVelocity);
      //shotVel.add(vel);
      shotVel.rotate(radians(random(-shotSpread, shotSpread)));

      list.add(new Shot(pos.x, pos.y, shotVel));
      coolDown = 100;
    }
  }

  public void display() {
    pushMatrix();
    {
      translate(0, 0, pos.z+1);
      rotateZ(PI/2+lookangle.heading());
      //scale(2);
      image(sprite, 0, 0);
    }
    popMatrix();
  }
}
  public void settings() {  size(1120, 840, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "BomberDX" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
