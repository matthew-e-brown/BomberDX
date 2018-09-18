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

  void updateShot(PVector targpos) {
    pos.add(vel);
    timeAlive++;

    displayPos.x = -targpos.x + pos.x;
    displayPos.y = -targpos.y + pos.y;
    displayPos.z = pos.z;
  }

  void displayShot() {
    pushMatrix();
    {
      translate(displayPos.x, displayPos.y, displayPos.z);
      rotateZ(PI/2+vel.heading());
      stroke(0);
      strokeWeight(0.75);
      fill(#FFF7CB);
      box(6, 15, 6);
    }
    popMatrix();
  }
  
  void displayShadow(float depth) {
    pushMatrix(); 
    {
      translate(displayPos.x+map(pos.z, 120, depth, -depth*0.2, 0), displayPos.y+map(pos.z, 120, depth, -depth*0.2, 0), depth);
      scale(SCALE);
      rotate(PI/2+vel.heading());

      rectMode(CENTER);
      fill(0, 0, 0, 128);
      rect(0, 0, 5, 5);
    }
    popMatrix();
  }
  
  void kill() {
    dead = !dead;
  }
  
}
