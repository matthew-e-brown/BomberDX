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
    this.thrust = 0.05;

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

  void update() {
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
      vel.z -= 0.1;
      //If it dies on an even frame, it spins one way, and another if it dies on an odd frame
      if (timeAlive % 2 == 0) deadAngle += 2.25;
      else deadAngle -= 2.25;
      deadAngle = deadAngle % 360;
      pos.add(vel);
    }
  }
  
  void kill() {
    deadAngle = degrees(vel.heading());
    dead = !dead;
  }
  
}
