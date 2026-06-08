package columns.model;

import columns.model.kernel.ModelListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Fake ModelListener that records every callback for assertion in tests.
 */
class FakeModelListener implements ModelListener {

    int lastLevel = -1;
    int levelChangedCount = 0;

    long lastScore = -1;
    int scoreUpdatedCount = 0;

    int fieldUpdatedCount = 0;
    int[][] lastField;

    // Each triplet detection recorded as int[6]: a,b,c,d,i,j
    List<int[]> detectedTriplets = new ArrayList<>();

    @Override
    public void levelHasChanged(int level) {
        lastLevel = level;
        levelChangedCount++;
    }

    @Override
    public void tripletDetected(int a, int b, int c, int d, int i, int j) {
        detectedTriplets.add(new int[]{a, b, c, d, i, j});
    }

    @Override
    public void fieldWasUpdated(int[][] newField) {
        lastField = newField;
        fieldUpdatedCount++;
    }

    @Override
    public void scoreUpdated(long score) {
        lastScore = score;
        scoreUpdatedCount++;
    }
}
