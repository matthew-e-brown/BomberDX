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

  void update() {
    //update the cooldown
    if (coolDown > 0) {
      coolDown -= fireRate;
    }

    //aim at the mouse
    lookangle = new PVector(mouseX, mouseY);
    lookangle.sub(new PVector(width/2, height/2));
  }

  void shoot(ArrayList list) {
    if (coolDown <= 0) {

      shotVel = lookangle;
      shotVel.setMag(muzzleVelocity);
      //shotVel.add(vel);
      shotVel.rotate(radians(random(-shotSpread, shotSpread)));

      list.add(new Shot(pos.x, pos.y, shotVel));
      coolDown = 100;
    }
  }

  void display() {
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
