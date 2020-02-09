package engine.graphics.world.chunk;

import engine.graphics.vertex.VertexDataBuf;

public class BakeChunkThread extends Thread {

    private final VertexDataBuf buffer = VertexDataBuf.create(0x200000);

    public BakeChunkThread(Runnable target, String name) {
        super(target, name);
    }

    public VertexDataBuf getBuffer() {
        return buffer;
    }
}