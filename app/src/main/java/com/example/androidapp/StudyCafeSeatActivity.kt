package com.example.androidapp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidapp.adapter.StudyCafeSeatAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.data.StudyCafeSeat
import com.example.androidapp.databinding.DialogStudyCafePaymentBinding
import com.example.androidapp.databinding.ActivityStudyCafeSeatsBinding

class StudyCafeSeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyCafeSeatsBinding
    private lateinit var seatAdapter: StudyCafeSeatAdapter
    private var selectedSeatNumber: Int = 7
    private var isMoveMode: Boolean = false
    private var notificationDownX: Float = 0f
    private var notificationAutoDismiss: Runnable? = null
    private val placeName = "랭스터디카페 성신여대점"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyCafeSeatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = placeName
        setupRecyclerView()
        setupInAppNotificationDismiss()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            val selectedSeat = seatAdapter.selectedSeat() ?: return@setOnClickListener
            when {
                seatAdapter.isReservedSeat(selectedSeat.number) -> showReservedSeatActions(selectedSeat)
                selectedSeat.status == RoomStatus.AVAILABLE && seatAdapter.hasReservedSeat() -> moveSeat(selectedSeat)
                selectedSeat.status == RoomStatus.AVAILABLE -> showPaymentDialog(selectedSeat)
                else -> {
                NotificationStore.saveSeatAlert(this, selectedSeat.number)
                showInAppNotification(
                    title = "좌석 알림 신청",
                    message = "${selectedSeat.number}번 좌석이 공석이 되면 알려드릴게요.",
                )
            }
        }
        }
    }

    override fun onStart() {
        super.onStart()
        seatAdapter.startCountdown()
    }

    override fun onStop() {
        seatAdapter.stopCountdown()
        super.onStop()
    }

    private fun setupRecyclerView() {
        val seats = MockData.studyCafeSeatLayout()
        selectedSeatNumber = seats.filterNotNull()
            .firstOrNull { it.status == RoomStatus.AVAILABLE }
            ?.number ?: 1

        seatAdapter = StudyCafeSeatAdapter { seat ->
            selectedSeatNumber = seat.number
            seatAdapter.updateSelection(seat.number)
            updateSelectedSeatInfo(seat)
            if (seatAdapter.isReservedSeat(seat.number)) {
                showReservedSeatActions(seat)
            }
        }

        binding.rvSeats.layoutManager = GridLayoutManager(this, SEAT_SPAN_COUNT)
        binding.rvSeats.adapter = seatAdapter
        seatAdapter.submitList(seats, selectedSeatNumber)
        seatAdapter.selectedSeat()?.let(::updateSelectedSeatInfo)
    }

    private fun updateSelectedSeatInfo(seat: StudyCafeSeat) {
        val seatLabel = seatLabel(seat)
        binding.tvSelectedSeatInfo.text = seatLabel
        when {
            seatAdapter.isReservedSeat(seat.number) -> {
                binding.tvSelectedSeatInfo.text = "$seatLabel · 내 자리"
                binding.tvSelectedActionInfo.text = "남은 시간 ${formatRemainingTime(seatAdapter.reservedRemainingSeconds())}"
                binding.tvRegisterText.text = "자리 관리하기"
            }
            seat.status == RoomStatus.AVAILABLE && seatAdapter.hasReservedSeat() -> {
                binding.tvSelectedActionInfo.text = if (isMoveMode) {
                    "이 좌석으로 자리를 옮길 수 있습니다."
                } else {
                    "빈 좌석입니다. 현재 자리에서 이곳으로 이동할 수 있습니다."
                }
                binding.tvRegisterText.text = "선택한 자리로 옮기기"
            }
            seat.status == RoomStatus.AVAILABLE -> {
                binding.tvSelectedActionInfo.text = "지금 예약할 수 있는 좌석입니다."
                binding.tvRegisterText.text = "선택한 좌석 결제하기"
            }
            else -> {
                binding.tvSelectedActionInfo.text = "사용 중인 좌석입니다. 자리가 나면 알림을 받을 수 있습니다."
                binding.tvRegisterText.text = "선택한 자리 알림받기"
            }
        }
    }

    private fun showPaymentDialog(seat: StudyCafeSeat) {
        val seatLabel = seatLabel(seat)
        val dialogBinding = DialogStudyCafePaymentBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        var selectedPass = PaymentPass.ONE_HOUR
        dialogBinding.tvPaymentPlace.text = placeName
        dialogBinding.tvPaymentSeat.text = seatLabel
        dialogBinding.tvPaymentAmount.text = selectedPass.priceText
        dialogBinding.rgPaymentPass.setOnCheckedChangeListener { _, checkedId ->
            selectedPass = when (checkedId) {
                dialogBinding.rbPass2Hour.id -> PaymentPass.TWO_HOURS
                dialogBinding.rbPass3Hour.id -> PaymentPass.THREE_HOURS
                else -> PaymentPass.ONE_HOUR
            }
            dialogBinding.tvPaymentAmount.text = selectedPass.priceText
        }
        dialogBinding.btnPaymentCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnPaymentConfirm.setOnClickListener {
                dialog.dismiss()
                seatAdapter.markReservedSeat(seat.number, selectedPass.durationSeconds)
                NotificationStore.savePaymentCompleteAlert(
                    context = this,
                    placeName = placeName,
                    seatLabel = seatLabel,
                    startTime = selectedPass.label,
                )
                showInAppNotification(
                    title = "결제 완료",
                    message = "$seatLabel 결제가 완료되었습니다.",
                )
                binding.tvSelectedSeatInfo.text = "$seatLabel · 내 자리"
                binding.tvSelectedActionInfo.text = "결제가 완료되었습니다. 남은 시간 ${formatRemainingTime(selectedPass.durationSeconds)}"
                binding.tvRegisterText.text = "자리 관리하기"
                Toast.makeText(this, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    private fun showReservedSeatActions(seat: StudyCafeSeat) {
        val seatLabel = seatLabel(seat)
        AlertDialog.Builder(this)
            .setTitle("내 자리 관리")
            .setMessage("$seatLabel\n남은 시간 ${formatRemainingTime(seatAdapter.reservedRemainingSeconds())}")
            .setNegativeButton("닫기", null)
            .setNeutralButton("자리 옮기기") { _, _ ->
                isMoveMode = true
                binding.tvSelectedActionInfo.text = "옮길 빈 좌석을 선택해 주세요."
                binding.tvRegisterText.text = "빈 좌석 선택 중"
                Toast.makeText(this, "옮길 빈 좌석을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("시간 연장") { _, _ ->
                showExtensionDialog(seat)
            }
            .show()
    }

    private fun showExtensionDialog(seat: StudyCafeSeat) {
        val options = arrayOf("1시간 연장 · 2,000원", "2시간 연장 · 4,000원", "3시간 연장 · 6,000원")
        val passes = arrayOf(PaymentPass.ONE_HOUR, PaymentPass.TWO_HOURS, PaymentPass.THREE_HOURS)
        AlertDialog.Builder(this)
            .setTitle("시간 연장")
            .setItems(options) { _, which ->
                val pass = passes[which]
                seatAdapter.extendReservedTime(pass.durationSeconds)
                NotificationStore.saveSeatExtensionAlert(
                    context = this,
                    placeName = placeName,
                    seatLabel = seatLabel(seat),
                    extraHours = pass.hours,
                )
                showInAppNotification(
                    title = "이용 시간 연장 완료",
                    message = "${pass.hours}시간 연장되었습니다.",
                )
                updateSelectedSeatInfo(seat)
                Toast.makeText(this, "${pass.hours}시간 연장되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun moveSeat(toSeat: StudyCafeSeat) {
        val fromSeatNumber = seatAdapter.reservedSeatNumber() ?: return
        val fromSeat = MockData.studyCafeSeatLayout().filterNotNull().firstOrNull { it.number == fromSeatNumber }
        val fromLabel = fromSeat?.let(::seatLabel) ?: "${fromSeatNumber}번 좌석"
        val toLabel = seatLabel(toSeat)
        seatAdapter.moveReservedSeat(toSeat.number)
        isMoveMode = false
        NotificationStore.saveSeatMoveAlert(
            context = this,
            placeName = placeName,
            fromSeatLabel = fromLabel,
            toSeatLabel = toLabel,
        )
        showInAppNotification(
            title = "자리 이동 완료",
            message = "$fromLabel → $toLabel",
        )
        binding.tvSelectedSeatInfo.text = "$toLabel · 내 자리"
        binding.tvSelectedActionInfo.text = "자리 이동이 완료되었습니다. 남은 시간 ${formatRemainingTime(seatAdapter.reservedRemainingSeconds())}"
        binding.tvRegisterText.text = "자리 관리하기"
        Toast.makeText(this, "자리 이동이 완료되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun seatLabel(seat: StudyCafeSeat): String = "${seat.number}번 ${seat.zone.label} 좌석"

    private fun formatRemainingTime(totalSeconds: Long): String {
        val hours = totalSeconds / 3_600
        val minutes = (totalSeconds % 3_600) / 60
        val seconds = totalSeconds % 60
        return "%d:%02d:%02d".format(hours, minutes, seconds)
    }

    private enum class PaymentPass(val hours: Int, val label: String, val priceText: String, val durationSeconds: Long) {
        ONE_HOUR(1, "1시간 이용권", "2,000원", 3_600L),
        TWO_HOURS(2, "2시간 이용권", "4,000원", 7_200L),
        THREE_HOURS(3, "3시간 이용권", "6,000원", 10_800L),
    }

    companion object {
        private const val SEAT_SPAN_COUNT = 18
    }

    private fun setupInAppNotificationDismiss() {
        binding.layoutInAppNotification.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    notificationDownX = event.rawX
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - notificationDownX
                    view.translationX = deltaX
                    view.alpha = (1f - kotlin.math.min(kotlin.math.abs(deltaX) / view.width, 0.7f)).coerceAtLeast(0.3f)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val deltaX = event.rawX - notificationDownX
                    if (kotlin.math.abs(deltaX) > view.width * 0.28f) {
                        hideInAppNotification(deltaX > 0)
                    } else {
                        view.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(160L)
                            .start()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showInAppNotification(title: String, message: String) {
        val notification = binding.layoutInAppNotification
        notification.animate().cancel()
        notificationAutoDismiss?.let(notification::removeCallbacks)
        binding.tvInAppNotificationTitle.text = title
        binding.tvInAppNotificationMessage.text = message
        notification.visibility = View.VISIBLE
        notification.translationX = 0f
        notification.translationY = -24f
        notification.alpha = 0f
        notification.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(220L)
            .start()
        val dismissRunnable = Runnable {
            if (notification.visibility == View.VISIBLE) {
                hideInAppNotification(toRight = true)
            }
        }
        notificationAutoDismiss = dismissRunnable
        notification.postDelayed(dismissRunnable, 3_500L)
    }

    private fun hideInAppNotification(toRight: Boolean) {
        val notification = binding.layoutInAppNotification
        notificationAutoDismiss?.let(notification::removeCallbacks)
        notificationAutoDismiss = null
        val targetX = if (toRight) notification.width.toFloat() else -notification.width.toFloat()
        notification.animate()
            .translationX(targetX)
            .alpha(0f)
            .setDuration(180L)
            .withEndAction {
                notification.visibility = View.GONE
                notification.translationX = 0f
                notification.translationY = -24f
            }
            .start()
    }
}
