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
    vel.x *= 0.65;
    vel.y *= 0.65;
    acc = new PVector(-vel.x*0.002, -vel.y*0.002, -0.2);
    for (int i = 0; i < stages.length; i++) {
      stages[i] = bombStages[i];
    }
    sprite = stages[0];
  }

  void update(PVector dropperpos) {
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
  
  void drawsprite(float x, float y) {
    image(sprite, x, y);
  }

  void display() {
    pushMatrix();
    {
      translate(displaypos.x, displaypos.y, pos.z);
      rotate(PI/2+vel.heading());
      scale(SCALE);
      drawsprite(0, 0);
    }
    popMatrix();
  }
  
  void displayShadow(float depth) {
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
