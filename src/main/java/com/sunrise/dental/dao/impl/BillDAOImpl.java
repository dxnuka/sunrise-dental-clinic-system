package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.db.DBConnectionManager;
import com.sunrise.dental.model.Bill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements BillDAO {

    private static final String SELECT_SQL =
            "SELECT b.bill_id, b.appointment_id, b.base_fee, b.consultation_fee, b.total_amount, " +
            "b.payment_status, b.generated_at, a.appointment_number " +
            "FROM bills b JOIN appointments a ON b.appointment_id = a.appointment_id ";

    @Override
    public Bill generateBill(int appointmentId) {
        String call = "{CALL sp_generate_bill(?,?,?)}";
        int billId;
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = con.prepareCall(call)) {
            cs.setInt(1, appointmentId);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.registerOutParameter(3, Types.DECIMAL);
            cs.execute();
            billId = cs.getInt(2);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating bill: " + e.getMessage(), e);
        }
        return findByBillId(billId);
    }

    private Bill findByBillId(int billId) {
        String sql = SELECT_SQL + "WHERE b.bill_id = ?";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading generated bill: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Bill findByAppointmentNumber(String appointmentNumber) {
        String sql = SELECT_SQL + "WHERE a.appointment_number = ? ORDER BY b.bill_id DESC LIMIT 1";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding bill: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Bill> findOutstanding() {
        List<Bill> list = new ArrayList<>();
        String sql = SELECT_SQL + "WHERE b.payment_status = 'UNPAID'";
        try (Connection con = DBConnectionManager.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error loading outstanding bills: " + e.getMessage(), e);
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setAppointmentNumber(rs.getString("appointment_number"));
        b.setBaseFee(rs.getBigDecimal("base_fee"));
        b.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setPaymentStatus(rs.getString("payment_status"));
        Timestamp ts = rs.getTimestamp("generated_at");
        if (ts != null) b.setGeneratedAt(ts.toLocalDateTime());
        return b;
    }
}
