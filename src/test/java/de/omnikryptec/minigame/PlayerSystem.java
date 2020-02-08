/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.minigame;

import org.joml.Vector2f;

import de.omnikryptec.core.Omnikryptec;
import de.omnikryptec.ecs.Entity;
import de.omnikryptec.ecs.Family;
import de.omnikryptec.ecs.IECSManager;
import de.omnikryptec.ecs.component.ComponentMapper;
import de.omnikryptec.ecs.component.ComponentType;
import de.omnikryptec.ecs.system.AbstractComponentSystem;
import de.omnikryptec.libapi.exposed.input.InputManager;
import de.omnikryptec.minigame.ShootEvent.Projectile;
import de.omnikryptec.util.profiling.Profiler;
import de.omnikryptec.util.settings.keys.KeysAndButtons;
import de.omnikryptec.util.updater.Time;

public class PlayerSystem extends AbstractComponentSystem {
    
    public PlayerSystem() {
        super(Family.of(ComponentType.of(MovementComponent.class), ComponentType.of(PlayerComponent.class),
                ComponentType.of(PositionComponent.class)));
    }
    
    private final InputManager inputManager = Omnikryptec.getInput();
    
    private final ComponentMapper<MovementComponent> movMapper = new ComponentMapper<>(MovementComponent.class);
    private final ComponentMapper<PlayerComponent> playMapper = new ComponentMapper<>(PlayerComponent.class);
    private final ComponentMapper<PositionComponent> posMapper = new ComponentMapper<>(PositionComponent.class);
    
    private float again;
    
    @Override
    public void update(final IECSManager iecsManager, final Time time) {
        Profiler.begin("PlayerSystem");
        this.again += time.deltaf;
        for (final Entity e : this.entities) {
            final MovementComponent mov = this.movMapper.get(e);
            final PlayerComponent play = this.playMapper.get(e);
            final PositionComponent plus = this.posMapper.get(e);
            RendererSystem.CAMERA.getTransform().localspaceWrite().setTranslation(-plus.transform.worldspacePos().x(),
                    -plus.transform.worldspacePos().y(), 0);
            float vy = 0;
            float vx = 0;
            if (this.inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_W)) {
                vy += play.maxYv;
            }
            if (this.inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_S)) {
                vy -= play.maxYv;
            }
            if (this.inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_A)) {
                vx -= play.maxXv;
            }
            if (this.inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_D)) {
                vx += play.maxXv;
            }
            if (this.inputManager.isKeyboardKeyPressed(KeysAndButtons.OKE_KEY_LEFT_CONTROL)) {
                vx /= 2;
                vy /= 2;
            }
            mov.dx = vx;
            mov.dy = vy;
            Omnikryptec.getAudio().setListenerPosition(plus.transform.worldspacePos().x() / 20,
                    plus.transform.worldspacePos().y() / 20, -5);
            Omnikryptec.getAudio().setListenerVelocity(vx / 20, vy / 20, 0);
            if (this.inputManager.isMouseButtonPressed(KeysAndButtons.OKE_MOUSE_BUTTON_1)
                    && this.inputManager.isMouseInsideViewport() && this.again > 0.15f) {
                this.again = 0;
                final Vector2f dir = this.inputManager.getMousePositionInWorld2D(RendererSystem.CAMERA, null);
                dir.add(-plus.transform.worldspacePos().x(), -plus.transform.worldspacePos().y());
                dir.normalize(200);
                dir.add(mov.dx, mov.dy);
                Omnikryptec.getEventBus().post(new ShootEvent(plus.transform.worldspacePos().x() + play.shOffsetX,
                        plus.transform.worldspacePos().y() + play.shOffsetY, dir, 1000, Projectile.Bomb));
            }
        }
        Profiler.end();
    }
    
}
