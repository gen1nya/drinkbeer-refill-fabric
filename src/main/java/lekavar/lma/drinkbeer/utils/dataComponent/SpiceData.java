package lekavar.lma.drinkbeer.utils.dataComponent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record SpiceData(int spiceA, int spiceB, int spiceC) {
    public static final Codec<SpiceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("spiceA").forGetter(SpiceData::spiceA),
                    Codec.INT.fieldOf("spiceB").forGetter(SpiceData::spiceB),
                    Codec.INT.fieldOf("spiceC").forGetter(SpiceData::spiceC)
            ).apply(instance, SpiceData::new)
    );
    public static final StreamCodec<ByteBuf, SpiceData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SpiceData::spiceA,
            ByteBufCodecs.INT, SpiceData::spiceB,
            ByteBufCodecs.INT, SpiceData::spiceC,
            SpiceData::new
    );

    /**
     * ФИКС ОТНОСИТЕЛЬНО АПСТРИМА: апстрим читает spiceList.get(1) и get(2) без проверки
     * длины, поэтому первая же добавленная специя (список из 1 элемента) роняла тик
     * с IndexOutOfBoundsException. Каскадная семантика сохранена: пустая специя
     * обнуляет все последующие.
     */
    public static SpiceData fromSpiceList(List<Integer> spiceList){
        int a = at(spiceList, 0), b = at(spiceList, 1), c = at(spiceList, 2);
        if (a <= 0) {
            b = 0;
            c = 0;
        } else if (b <= 0) {
            c = 0;
        }
        return new SpiceData(a,b,c);
    }

    private static int at(List<Integer> spiceList, int index) {
        if (index >= spiceList.size()) return 0;
        Integer value = spiceList.get(index);
        return value != null && value > 0 ? value : 0;
    }

}
