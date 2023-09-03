package me.alenalex.notaprisoncore.api.provider;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public interface IRandomProvider {
    final Random RANDOM = new Random();

    static <E> E getRandomElementFrom(Collection<E> collection){
        return collection.stream()
                .skip((int) (collection.size() * Math.random()))
                .findFirst().get();
    }

    static <E> E getRandomFromList(List<E> list){
        return list.get(RANDOM.nextInt(list.size()));
    }

}
