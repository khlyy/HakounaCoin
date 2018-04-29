
public class BlockAnnouncement extends Announcement {
    public Block getBlock() {
        return block;
    }

    private Block block;
    public BlockAnnouncement(Block block) {

        this.block = block;
    }
}
