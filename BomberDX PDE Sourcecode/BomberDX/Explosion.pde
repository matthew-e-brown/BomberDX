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

  void update() {
    s++;
    if (s >= spriteStages.length) dead = true;
    if (!dead) sprite = spriteStages[s];
  }

  void display(PVector player) {
    pushMatrix();
    {
      translate(-player.x + pos.x, -player.y + pos.y, pos.z);
      imageMode(CENTER);
      scale(scale);
      image(sprite, 0, 0);
    }
    popMatrix();
  }

  void displayShadow(PVector player, float depth) {
    pushMatrix(); 
    {
      translate(-player.x + pos.x +map(pos.z, 120, depth, -depth*0.2, 0), -player.y + pos.y + map(pos.z, 120, depth, -depth*0.2, 0), depth);
      scale(scale);

      imageMode(CENTER);
      tint(0, 0, 0, 128);
      image(sprite, 0, 0);
      tint(255, 255);
    }
    popMatrix();
  }
}
