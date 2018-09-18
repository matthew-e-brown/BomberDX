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

void setup() {
  size(1120, 840, P3D); //P3D is used so we can get shadows and the ground is farther away.
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

void draw() {
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
      text(slider.title, slider.x, slider.y - slider.h*1.5);
      slider.update();
      slider.display();
    }

    fill(0);
    textAlign(CENTER, CENTER);    
    textFont(bold);
    textSize(30);
    text(planeToggle.title, planeToggle.x, planeToggle.y - planeToggle.h*1.5);
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
    text("@MattShnoop", width/2-35*8, height/2-35*2.25);
    text("@drunkbear95", width/2+35*8, height/2-35*2.25);
    text("@neosndt", width/2-35*8, height/2+35*1.75);

    textFont(light);
    textSize(28);
    text("Lead Programmer, Project Head", width/2-35*8, height/2-35*1.5);
    text("Programmer", width/2+35*8, height/2-35*1.5);
    text("Design Coordinator, Graphics", width/2-35*8, height/2+35*2.5);
    text("Supplementary Explosion Graphic", width/2+35*8, height/2+35*2.5);
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
      fill(#FF0D0D);
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
          if (dist(proj.pos.x, proj.pos.y, player.pos.x, player.pos.y) <= player.sprite.width*0.75) {
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
            explosions.add(new Explosion(plane.pos.x, plane.pos.y, plane.pos.z, 0.5));
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
            explosions.add(new Explosion(plane.pos.x, plane.pos.y, grounddepth+1, 1.2));
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
        for (Explosion explosion : explosions) if (explosion.pos.z > grounddepth + 1.5) explosion.displayShadow(player.pos, grounddepth+1);

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

void updateGrid(Tile[][] g, PlayerPlane player) {
  for (int i = 0; i < g.length; i++) {
    for (int j = 0; j < g[0].length; j++) {
      g[i][j].displaypos.x = -player.pos.x-(128*g.length/2)+128*i;
      g[i][j].displaypos.y = -player.pos.y-(128*g[0].length/2)+128*j;
    }
  }
}

void remainingBases(float x, float y) {
  pushMatrix(); 
  {
    translate(x, y, 1);
    rectMode(CORNER);
    image(baseIcon, 0, 0);
    for (int i = 0; i < totalBases; i++) {
      stroke(0);
      strokeWeight(2);
      if (i < totalBases - basesKilled) fill(lerpColor(#5512DE, #5FC4EE, map(i, 0, totalBases, 0, 1)));
      else fill(50);
      rect(baseIcon.width + 16 + 12 * i, 0, 10, 25, 3);
    }
  }  
  popMatrix();
}

void mainMenuSetup() {// // --- Main menu setup
  //Button(float x, float y, float w, float h, String text, float size) {
  mB[playButton] = new Button(560, 367.5, 560, 105, "Play Game", 30, "playgame");
  mB[opButton] = new Button(560, 507.5, 560, 105, "Options", 30, "options");
  mB[credButton] = new Button(437.5, 647.5, 315, 105, "Credits", 30, "credits");
  mB[quitButton] = new Button(735, 647.5, 210, 105, "Quit", 30, "quit");
}// // --- end of main menu setup

void optionsMenuSetup() {// // --- Options menu setup
  oS[distSlider] = new floatSlider(787.5, 405, 305, 30, "Render Distance", "render");
  oS[maxPlane] = new floatSlider(787.5, 510, 305, 30, "Max Enemy Planes", "max");

  oS[distSlider].setVar(RENDERDIST, 0, 9);
  oS[maxPlane].setVar(maxPlanes, 4, 20);

  oB[backButton] = new Button(560, 647.5, 210, 105, "Back", 30, "back");
  planeToggle = new Switch(332.5, 457.5, 160, 30, "Pick a Plane", 18);
  planeToggle.setVar(curPlane, "B17", "B29");
}// // --- end of options menu setup

void mainGameSetup() {// // --- Main game setup
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
  pB[resume] = new Button(560, 367.5, 350, 105, "Resume", 30, "resume");
  pB[resume].centre = true;
  pB[resume].textInvert = true;
  pB[home] = new Button(560, 647.5, 350, 105, "Main Menu", 30, "mainmenu");
  pB[home].centre = true;
  pB[home].textInvert = true;
  pB[restart] = new Button(560, 507.5, 350, 105, "Restart", 30, "restart");
  pB[restart].centre = true;
  pB[restart].textInvert = true;
  pB[quit] = new Button(70, 70, 70, 70, "", 1, "quit1");

  //Button[] eB = new Button[3];
  //int eBrestart = 0, eBmenu = 1, eBquit = 2;
  eB[eBrestart] = new Button(560, 507.5, 350, 105, "Restart", 30, "restart");
  eB[eBrestart].centre = true;
  eB[eBrestart].textInvert = true;
  eB[eBmenu] = new Button(560, 647.5, 350, 105, "Main Menu", 30, "mainmenu");
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

void createGrid() {
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

void loadExplosionImages() {
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

void loadBombImages() {
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

void loadPlaneImages() {
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

void loadDeadPlaneImages() {
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
