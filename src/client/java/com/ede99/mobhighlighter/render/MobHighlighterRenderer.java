package com.ede99.mobhighlighter.render;

import com.ede99.mobhighlighter.Config;
import com.ede99.mobhighlighter.util.ModState;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class MobHighlighterRenderer {

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(MobHighlighterRenderer::renderMobBoxes);
    }

    private static void renderMobBoxes(WorldRenderContext context) {
        if (!ModState.isHighlightEnabled()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        MatrixStack matrices = context.matrixStack();
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        // Use custom render distance instead of view distance for better performance control
        double maxDistance = ModState.getMaxRenderDistance();
        double maxDistanceSq = maxDistance * maxDistance;

        // Single pass through entities - collect valid entities first
        List<Entity> entitiesToRender = new ArrayList<>();
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            if (entity == client.player) continue; // Skip the current player
            String entityName = entity instanceof PlayerEntity ? "Player" : livingEntity.getType().getName().getString();
            if (!Config.getState(entityName, false)) continue;
            if (entity.squaredDistanceTo(client.player) > maxDistanceSq) continue;
            entitiesToRender.add(entity);
        }
        
        if (entitiesToRender.isEmpty()) return;

        matrices.push();

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Get camera direction for line drawing using the correct method
        Vec3d cameraDir = calculateCameraDirection(camera);
        Vec3d cameraPos = camera.getPos().add(cameraDir.multiply(0.5)); // Start line slightly in front of camera

        // Render only the collected entities
        for (Entity entity : entitiesToRender) {
            try {
                String entityName = entity instanceof PlayerEntity ? "Player" : ((LivingEntity) entity).getType().getName().getString();
                Box box = entity.getBoundingBox().offset(-camPos.x, -camPos.y, -camPos.z);
                int color = Config.getColor(entityName, 0xFF00FF00);

                drawSolidBox(matrix, buffer, box, color);

                // Draw thick line from camera direction to entity center
                Vec3d entityCenter = entity.getBoundingBox().getCenter().subtract(camPos);
                drawThickLine(matrix, buffer, cameraPos.subtract(camPos), entityCenter, color, 0.002f);
            } catch (Exception e) {
                // Skip problematic entities to prevent crashes
                continue;
            }
        }

        // End and draw the buffer
        BuiltBuffer builtBuffer = buffer.end();
        if (builtBuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtBuffer);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static Vec3d calculateCameraDirection(Camera camera) {
        // Calculate camera direction from yaw and pitch
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        // Convert to radians
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Calculate direction vector
        float x = (float) (-Math.sin(yawRad) * Math.cos(pitchRad));
        float y = (float) (-Math.sin(pitchRad));
        float z = (float) (Math.cos(yawRad) * Math.cos(pitchRad));

        return new Vec3d(x, y, z).normalize();
    }

    private static void drawSolidBox(Matrix4f matrix, BufferBuilder buffer, Box box, int color) {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = 0.3F;

        float minX = (float) box.minX;
        float minY = (float) box.minY;
        float minZ = (float) box.minZ;
        float maxX = (float) box.maxX;
        float maxY = (float) box.maxY;
        float maxZ = (float) box.maxZ;

        addQuad(buffer, matrix, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
        addQuad(buffer, matrix, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, r, g, b, a);
        addQuad(buffer, matrix, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ, r, g, b, a);
        addQuad(buffer, matrix, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
        addQuad(buffer, matrix, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);
        addQuad(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ, r, g, b, a);
    }

    private static void addQuad(BufferBuilder buffer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float r, float g, float b, float a) {
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
        buffer.vertex(matrix, x3, y3, z3).color(r, g, b, a);
        buffer.vertex(matrix, x4, y4, z4).color(r, g, b, a);
    }

    private static void drawThickLine(Matrix4f matrix, BufferBuilder buffer, Vec3d start, Vec3d end, int color, float thickness) {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = 1.0F;

        // Calculate direction vector
        Vec3d dir = end.subtract(start).normalize();

        // Calculate perpendicular vectors for thickness
        Vec3d perp1 = new Vec3d(-dir.z, 0, dir.x).normalize().multiply(thickness);
        Vec3d perp2 = perp1.crossProduct(dir).normalize().multiply(thickness);

        // Create a thick line by drawing a quad around the line
        Vec3d[] corners = {
                start.add(perp1).add(perp2),
                start.add(perp1).subtract(perp2),
                start.subtract(perp1).subtract(perp2),
                start.subtract(perp1).add(perp2),
                end.add(perp1).add(perp2),
                end.add(perp1).subtract(perp2),
                end.subtract(perp1).subtract(perp2),
                end.subtract(perp1).add(perp2)
        };

        // Draw the sides of the thick line
        addQuad(buffer, matrix,
                (float) corners[0].x, (float) corners[0].y, (float) corners[0].z,
                (float) corners[1].x, (float) corners[1].y, (float) corners[1].z,
                (float) corners[5].x, (float) corners[5].y, (float) corners[5].z,
                (float) corners[4].x, (float) corners[4].y, (float) corners[4].z,
                r, g, b, a);

        addQuad(buffer, matrix,
                (float) corners[1].x, (float) corners[1].y, (float) corners[1].z,
                (float) corners[2].x, (float) corners[2].y, (float) corners[2].z,
                (float) corners[6].x, (float) corners[6].y, (float) corners[6].z,
                (float) corners[5].x, (float) corners[5].y, (float) corners[5].z,
                r, g, b, a);

        addQuad(buffer, matrix,
                (float) corners[2].x, (float) corners[2].y, (float) corners[2].z,
                (float) corners[3].x, (float) corners[3].y, (float) corners[3].z,
                (float) corners[7].x, (float) corners[7].y, (float) corners[7].z,
                (float) corners[6].x, (float) corners[6].y, (float) corners[6].z,
                r, g, b, a);

        addQuad(buffer, matrix,
                (float) corners[3].x, (float) corners[3].y, (float) corners[3].z,
                (float) corners[0].x, (float) corners[0].y, (float) corners[0].z,
                (float) corners[4].x, (float) corners[4].y, (float) corners[4].z,
                (float) corners[7].x, (float) corners[7].y, (float) corners[7].z,
                r, g, b, a);

        // Draw the ends of the thick line
        addQuad(buffer, matrix,
                (float) corners[0].x, (float) corners[0].y, (float) corners[0].z,
                (float) corners[1].x, (float) corners[1].y, (float) corners[1].z,
                (float) corners[2].x, (float) corners[2].y, (float) corners[2].z,
                (float) corners[3].x, (float) corners[3].y, (float) corners[3].z,
                r, g, b, a);

        addQuad(buffer, matrix,
                (float) corners[4].x, (float) corners[4].y, (float) corners[4].z,
                (float) corners[5].x, (float) corners[5].y, (float) corners[5].z,
                (float) corners[6].x, (float) corners[6].y, (float) corners[6].z,
                (float) corners[7].x, (float) corners[7].y, (float) corners[7].z,
                r, g, b, a);
    }
}