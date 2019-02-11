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

    turnspeed = 1.25;
    
    maxCoolDown = 500;
    coolDown = maxCoolDown;
    shotSpread = 10;
    fireRate = 10;
    shotVel = new PVector(0, 0);
    muzzleVelocity = 20;

    normalspeed = 8;
    maxspeed = 10;
    lowspeed = 6;

    SCALE = 0.80;

    followdist = random(225, 650);
    canShoot = true;
    
    timeSinceDisengage = 0;
    forceEngageCounter = 0;
    timeToReengage = 90;
    timeToForceDisengage = 200;
    engagePlayer = true;
  }

  void update(PVector targpos) {
    if (!dead) {
      faceEnemy(targpos);
      coolDown -= fireRate;

      PVector acc = vel.copy().setMag(thrust);

      //Set speed based on distance from player
      float dist = dist(pos.x, pos.y, targpos.x, targpos.y);

      if (dist > followdist*1.325 && vel.mag() < maxspeed) { //Far
        canShoot = true;
        if (vel.mag() < maxspeed) {
          vel.add(acc);
        }
      } else if (dist < followdist*0.10) { //Close
        canShoot = false;
        if (pos.z < 120) pos.z += 5;
        if (vel.mag() > lowspeed) {
          vel.sub(acc);
        }
      } else if (dist < followdist*1.325 && dist > followdist*0.45) { //In between
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
  
  void shoot(ArrayList list) {
      shotVel = vel.copy();
      shotVel.setMag(muzzleVelocity);
      //shotVel.mult(2);
      shotVel.rotate(radians(random(-shotSpread, shotSpread)));

      list.add(new Shot(pos.x, pos.y, shotVel));
      coolDown = maxCoolDown;
  }

  void display() {
    if (!dead) {
      pushMatrix(); 
      {
        translate(displaypos.x, displaypos.y, displaypos.z);
        scale(SCALE);
        rotate(PI/2+vel.heading());

        if (timeAlive < 0.85*frameRate) tint(255, 255, 255, map(timeAlive, 0, 0.85*frameRate, 0, 255)); 
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

  void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(displaypos.x+map(pos.z, 120, depth, -depth*0.2, 0), displaypos.y+map(pos.z, 120, depth, -depth*0.2, 0), depth);
      scale(SCALE);
      if (!dead) rotate(PI/2+vel.heading());
      else rotate(PI/2+radians(deadAngle));

      imageMode(CENTER);
      if (timeAlive < 0.85*frameRate) tint(0, 0, 0, map(timeAlive, 0, 0.85*frameRate, 0, 128));
      else tint(0, 0, 0, 128);
      image(sprite, 0, 0);
      tint(255, 255);
    }
    popMatrix();
  }

  int quadrant(float angle) {
    int quad = 0;
    if (angle <= 0 && angle > -PI/2) quad = 1;
    else if (angle <= -PI/2 && angle >= -PI) quad = 2;
    else if (angle >= PI/2 && angle <= PI) quad = 3;
    else if (angle > 0 && angle < PI/2) quad = 4;
    return quad;
  }

  int oppquad(int quad) {
    int opp = 0;
    if (quad == 1) opp = 3;
    else if (quad == 2) opp = 4;
    else if (quad == 3) opp = 1;
    else if (quad == 4) opp = 2;
    return opp;
  }

  float oppangle(float angle) {
    float opp = 0;
    if (angle < 0) opp = angle + PI;
    else if (angle > 0) opp = angle - PI;
    else if (angle == 0) opp = PI;
    else if (angle == PI) opp = 0;
    return opp;
  }

  float targangle;
  //returns a value from -PI to PI

  void faceEnemy(PVector targetpos) {
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

  void healthBar() {
    pushMatrix();
    {
      float h = sprite.height/6;
      float w = sprite.width*0.80;
      translate(displaypos.x, displaypos.y+sprite.height*1.2, displaypos.z);
      rectMode(CENTER);
      fill(50);
      noStroke();
      rect(0, 0, w, h, h/2);
      fill(#FF0D0D);
      rectMode(CORNER);
      rect(-w/2 + h/8, -h/2 + h/8, map(constrain(health, 0, maxHealth), maxHealth, 0, w - h/4, 0), h - h/4, h/2);
    }
    popMatrix();
  }
}
