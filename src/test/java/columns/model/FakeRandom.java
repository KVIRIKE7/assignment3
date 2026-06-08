package columns.model;

import columns.model.kernel.RandomGenerator;

/**
 * Deterministic fake RandomGenerator.
 * Returns values from a preset sequence, cycling if exhausted.
 */
class FakeRandom implements RandomGenerator {

    private final int[] values;
    private int index = 0;

    FakeRandom(int... values) {
        this.values = values;
    }

    @Override
    public int nextInt() {
        int v = values[index % values.length];
        index++;
        return v;
    }
}
