package com.example.math;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class InverseMatrixCalculator {
    public static double[][] pseudoInverse(double[][] matrix) {
        DMatrixRMaj mat = new DMatrixRMaj(matrix);
        DMatrixRMaj pinv = new DMatrixRMaj(mat.numCols, mat.numRows);
        CommonOps_DDRM.pinv(mat, pinv);
        double[][] result = new double[pinv.numRows][pinv.numCols];
        for (int i = 0; i < pinv.numRows; i++) {
            for (int j = 0; j < pinv.numCols; j++) {
                result[i][j] = pinv.get(i,j);
            }
        }
        return result;
    }
}
