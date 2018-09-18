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

    normalspeed = 2.25;
    maxspeed = 6.5;
    lowspeed = 1.5;

    SCALE = 1.2;

    lastBomb = -100;
    b = 0;
    bombCount = 25;
    maxBombs = bombCount;

    mainGun = new Turret(pos.x, pos.y);
    mainGun.pos = pos;
    mainGun.vel = vel;

    grounded = false;
  }

  void update() {
    super.update();
    if (!dead) {

      gridPosX = floor(pos.x/128) + floor(grid.length/2) + 1;
      gridPosY = floor(pos.y/128) + floor(grid.length/2) + 1;

      mainGun.update();

      if (spacebar && frameCount - lastBomb > frameRate/2.5 && bombCount > 0) {
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
                      else tileOver.health -= bombStrength * 0.25;
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
              explosions.add(new Explosion(bombs[i].pos.x, bombs[i].pos.y, grounddepth+1, 1.45));
            }
            bombs[i] = null;
          }
        }
      }

      if (frameCount - lastBomb > 2.5*frameRate && frameCount - lastRestore > 0.25*frameRate && bombCount < maxBombs) {
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

  void display() {
    for (Bomb b : bombs) {
      if (b != null) {
        b.displayShadow(grounddepth+1.2);
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

  void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(map(pos.z, 120, depth, -depth*0.2, 0), map(pos.z, 120, depth, -depth*0.2, 0), depth);
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

  void healthBar(float x, float y, float w, float h) {
    pushMatrix();
    {
      translate(x + w/2, y + h/2);
      rectMode(CENTER);
      fill(0);
      stroke(0);
      strokeWeight(2);
      rect(0, 0, w, h, h/2);
      fill(/*#FFC400*/#FF0D0D);
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
      image(healthIcon, -270, 0);
    }
    popMatrix();
  }

  void drawBombCounter(float x, float y) {
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
        if (i < bombCount) fill(lerpColor(/*#760000*/ #ECCA62, /*#FF0D0D*/ #F62932, map(i, 0, maxBombs, 0, 1)));
        else fill(50);
        rect(bombIcon.width + 20 + 12*i, 0, 10, 25, 3);
      }
    } 
    popMatrix();
  }
}
